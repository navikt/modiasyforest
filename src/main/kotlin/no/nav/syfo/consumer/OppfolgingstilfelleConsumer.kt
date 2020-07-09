package no.nav.syfo.consumer

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.config.consumer.SykmeldingerConfig
import no.nav.syfo.controller.domain.*
import no.nav.syfo.controller.domain.sykmelding.Periode
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.controller.domain.tidslinje.Hendelse
import no.nav.syfo.controller.domain.tidslinje.Hendelsestype
import no.nav.syfo.ereg.EregConsumer
import no.nav.syfo.ereg.Virksomhetsnummer
import no.nav.syfo.mappers.SykmeldingMapper
import no.nav.syfo.oidc.OIDCUtil.tokenFraOIDC
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentSykeforlopperiodeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSSykeforlopperiode
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeRequest
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentOppfoelgingstilfelleListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

@Service
class OppfolgingstilfelleConsumer @Inject constructor(
    @Value("\${dev}") private val dev: String,
    private val contextHolder: OIDCRequestContextHolder,
    private val aktorConsumer: AktorConsumer,
    private val eregConsumer: EregConsumer,
    private val naermesteLederConsumer: NaermesteLederConsumer,
    private val sykmeldingerConfig: SykmeldingerConfig,
    private val sykmeldingV1: SykmeldingV1,
    private val sykefravaersoppfoelgingV1: SykefravaersoppfoelgingV1
) {
    fun getOppfolgingstilfelle(fnr: String, oidcIssuer: String?): List<Sykeforloep> {
        if (StringUtils.isBlank(fnr) || !fnr.matches(Regex("\\d{11}$"))) {
            log.error("Prøvde å hente sykeforløp med fnr og oidcIssuer {}")
            throw IllegalArgumentException()
        }
        val aktoerId = aktorConsumer.hentAktoerIdForFnr(fnr)
        return try {
            val request = WSHentOppfoelgingstilfelleListeRequest()
                .withAktoerId(aktoerId)
            val response: WSHentOppfoelgingstilfelleListeResponse
            response = if ("true" == dev) {
                sykmeldingV1.hentOppfoelgingstilfelleListe(request)
            } else {
                val oidcToken = tokenFraOIDC(contextHolder, oidcIssuer)
                sykmeldingerConfig.hentOppfoelgingstilfelleListe(request, oidcToken)
            }
            response.oppfoelgingstilfelleListe
                .stream()
                .map { wsOppfoelgingstilfelle: WSOppfoelgingstilfelle -> tilSykeforloep(wsOppfoelgingstilfelle, fnr) }
                .collect(Collectors.toList())
        } catch (e: HentOppfoelgingstilfelleListeSikkerhetsbegrensning) {
            log.warn("Fikk sikkerhetsbegrensning ved henting av oppfolgingstilfelleListe for person", e)
            throw ForbiddenException()
        } catch (e: RuntimeException) {
            log.error("Fikk runtimeexception ved henting av oppfolgingstilfelleListe for person", e)
            throw e
        }
    }

    private fun tilSykeforloep(wsOppfoelgingstilfelle: WSOppfoelgingstilfelle, fnr: String): Sykeforloep {
        return Sykeforloep()
            .withHendelser(tilHendelser(wsOppfoelgingstilfelle.hendelseListe, fnr))
            .withSykmeldinger(tilSykmeldinger(wsOppfoelgingstilfelle.meldingListe))
            .withOppfolgingsdato(wsOppfoelgingstilfelle.oppfoelgingsdato)
            .withSluttdato(sluttdato(tilSykmeldinger(wsOppfoelgingstilfelle.meldingListe)))
    }

    fun sluttdato(sykmeldinger: List<Sykmelding>): LocalDate {
        return sykmeldinger
            .stream()
            .flatMap { sykmelding: Sykmelding -> sykmelding.mulighetForArbeid.perioder.stream() }
            .sorted { o1: Periode, o2: Periode -> o2.tom.compareTo(o1.tom) }
            .map { sykmelding: Periode -> sykmelding.tom }
            .findFirst().orElseThrow { NotFoundException() }
    }

    private fun tilSykmeldinger(meldinger: List<WSMelding>): List<Sykmelding> {
        return meldinger
            .stream()
            .map { sykmeldingerWS: WSMelding? -> SykmeldingMapper.sykmelding(sykmeldingerWS) }
            .collect(Collectors.toList())
    }

    private fun tilHendelser(hendelser: List<WSHendelse>, fnr: String): List<Hendelse> {
        val nyNaermesteLederHendelse = Predicate { h: WSHendelse -> h.type == WSHendelsestype.NY_NAERMESTE_LEDER }
        val generellHendelse = nyNaermesteLederHendelse.negate()
        val nyNaermesteLederHendelser = hendelser.stream().filter(nyNaermesteLederHendelse)
        val generelleHendelser = hendelser.stream().filter(generellHendelse)
        return Stream.concat(
            tilHendelseNyNaermesteLeder(nyNaermesteLederHendelser, fnr),
            tilHendelseGenerell(generelleHendelser)
        ).collect(Collectors.toList())
    }

    private fun tilHendelseGenerell(generelleHendelser: Stream<WSHendelse>): Stream<Hendelse> {
        return generelleHendelser.map { wsHendelse: WSHendelse -> tilHendelse(wsHendelse) }
    }

    private fun tilHendelseNyNaermesteLeder(wsHendelseStream: Stream<WSHendelse>, fnr: String): Stream<Hendelse> {
        return try {
            val naermesteLedere = naermesteLederConsumer.finnNarmesteLedere(fnr)
                .map { nl: NaermesteLeder ->
                    nl.withOrganisasjonsnavn(
                        eregConsumer.virksomhetsnavn(Virksomhetsnummer(nl.orgnummer))
                    )
                }
            val naermesteLedereMap: Map<Long, NaermesteLeder> = naermesteLedere
                .stream()
                .collect(Collectors.toMap({ obj: NaermesteLeder -> obj.hentId() }, { leder: NaermesteLeder -> leder }) { _: NaermesteLeder, l2: NaermesteLeder -> l2 })
            wsHendelseStream.map { wsHendelse: WSHendelse ->
                val wsHendelseNyNaermesteLeder = wsHendelse as WSHendelseNyNaermesteLeder
                val id = wsHendelseNyNaermesteLeder.naermesteLederId
                val leder = naermesteLedereMap[id]
                tilHendelse(wsHendelse)
                    .withData("naermesteLeder", leder)
            }
        } catch (e: RuntimeException) {
            log.error("Fikk runtimeexception ved henting av naermesteledere for person med fnr", e)
            Stream.empty()
        }
    }

    private fun tilHendelse(wsHendelse: WSHendelse): Hendelse {
        return Hendelse()
            .withInntruffetdato(wsHendelse.tidspunkt.toLocalDate())
            .withType(Hendelsestype.valueOf(wsHendelse.type.value()))
            .withTekstkey(fraHendelsetype(wsHendelse.type))
    }

    private fun fraHendelsetype(type: WSHendelsestype): String? {
        return when (type) {
            WSHendelsestype.AKTIVITETSKRAV_VARSEL -> "tidslinje.aktivitetskrav-varsel"
            WSHendelsestype.NY_NAERMESTE_LEDER -> "tidslinje.ny-naermeste-leder"
            else -> null
        }
    }

    fun hentOppfolgingstilfelleperioder(fnr: String, orgnummer: String): List<Oppfolgingstilfelle> {
        val aktorId = aktorConsumer.hentAktoerIdForFnr(fnr)
        val request = WSHentSykeforlopperiodeRequest()
            .withAktoerId(aktorId)
            .withOrgnummer(orgnummer)
        return try {
            val wsHentSykeforlopperiodeResponse = sykefravaersoppfoelgingV1.hentSykeforlopperiode(request)
            tilSykeforlopperiodeListe(wsHentSykeforlopperiodeResponse.sykeforlopperiodeListe, orgnummer)
        } catch (e: HentSykeforlopperiodeSikkerhetsbegrensning) {
            log.warn("Sikkerhetsbegrensning ved henting av oppfølgingstilfelleperioder")
            emptyList()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OppfolgingstilfelleConsumer::class.java)
    }
}

private fun tilSykeforlopperiodeListe(
    wsSykeforlopperiodeListe: List<WSSykeforlopperiode>,
    orgnummer: String
): List<Oppfolgingstilfelle> {
    return wsSykeforlopperiodeListe.map {
        Oppfolgingstilfelle(
            orgnummer = orgnummer,
            fom = it.fom,
            tom = it.tom,
            grad = it.grad,
            aktivitet = it.aktivitet
        )
    }
}