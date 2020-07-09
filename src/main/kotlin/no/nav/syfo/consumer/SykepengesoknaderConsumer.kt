package no.nav.syfo.consumer

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.config.consumer.SykepengesoknadConfig
import no.nav.syfo.controller.domain.sykepengesoknad.Sykepengesoknad
import no.nav.syfo.ereg.EregConsumer
import no.nav.syfo.ereg.Virksomhetsnummer
import no.nav.syfo.mappers.WS2SykepengesoknadMapper
import no.nav.syfo.oidc.OIDCUtil.tokenFraOIDC
import no.nav.syfo.utils.MapUtil
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.HentSykepengesoeknadListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeRequest
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@Service
class SykepengesoknaderConsumer @Inject constructor(
    @Value("\${dev}") private val dev: String,
    private val contextHolder: OIDCRequestContextHolder,
    private val eregConsumer: EregConsumer,
    private val sykepengesoeknadV1: SykepengesoeknadV1,
    private val sykepengesoknadConfig: SykepengesoknadConfig
) {
    @Cacheable(cacheNames = ["sykepengesoknad"], key = "#aktoerId.concat(#oidcIssuer)", condition = "#aktoerId != null && #oidcIssuer != null")
    fun hentSykepengesoknader(aktoerId: String, oidcIssuer: String): List<Sykepengesoknad> {
        if (StringUtils.isBlank(aktoerId) || !aktoerId.matches(Regex("\\d{13}$"))) {
            log.error("Pøvde å hente sykepengesoknader med aktoerId {}", aktoerId)
            throw IllegalArgumentException()
        }
        return try {
            val request = WSHentSykepengesoeknadListeRequest().withAktoerId(aktoerId)
            val response: WSHentSykepengesoeknadListeResponse
            response = if ("true" == dev) {
                sykepengesoeknadV1.hentSykepengesoeknadListe(request)
            } else {
                val oidcToken = tokenFraOIDC(contextHolder, oidcIssuer)
                sykepengesoknadConfig.hentSykepengesoeknadListe(request, oidcToken)
            }
            MapUtil.mapListe(response.sykepengesoeknadListe, WS2SykepengesoknadMapper.ws2Sykepengesoknad)
                .stream()
                .map(berikMedArbeidsgiverNavn)
                .collect(Collectors.toList())
        } catch (e: HentSykepengesoeknadListeSikkerhetsbegrensning) {
            log.error("Har ikke tilgang til søknadene det spørres om: {}", aktoerId, e)
            throw ForbiddenException()
        } catch (e: RuntimeException) {
            log.error("Det skjedde en runtimefeil da {} spurte om søknadene til: {}", aktoerId, e)
            throw e
        }
    }

    private val berikMedArbeidsgiverNavn = Function { soknad: Sykepengesoknad ->
        soknad
            .withArbeidsgiver(soknad.arbeidsgiver
                !!.withNavn(eregConsumer
                    .virksomhetsnavn(Virksomhetsnummer(soknad.arbeidsgiver!!.orgnummer))))
    }

    companion object {
        private val log = LoggerFactory.getLogger(SykepengesoknaderConsumer::class.java)
    }
}
