package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.config.CacheConfig.Companion.CACHENAME_SYFOSERVICE_LEDERE
import no.nav.syfo.consumer.AktorConsumer
import no.nav.syfo.ereg.EregConsumer
import no.nav.syfo.ereg.Virksomhetsnummer
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@Service
class NaermesteLederConsumer @Inject constructor(
    private val aktorConsumer: AktorConsumer,
    private val eregConsumer: EregConsumer,
    private val sykefravaersoppfoelgingV1: SykefravaersoppfoelgingV1
) {
    @Cacheable(cacheNames = [CACHENAME_SYFOSERVICE_LEDERE], key = "#fnr", condition = "#fnr != null")
    fun finnNarmesteLedere(fnr: String): List<NaermesteLeder> {
        val aktoerId = aktorConsumer.hentAktoerIdForFnr(fnr)
        return try {
            sykefravaersoppfoelgingV1.hentNaermesteLederListe(WSHentNaermesteLederListeRequest()
                .withAktoerId(aktoerId)
                .withKunAktive(false)
            )
                .naermesteLederListe
                .stream()
                .map { element: WSNaermesteLeder -> tilNaermesteLeder(element, eregConsumer.virksomhetsnavn(Virksomhetsnummer(element.orgnummer))) }
                .collect(Collectors.toList())
        } catch (e: HentNaermesteLederListeSikkerhetsbegrensning) {
            log.warn("Fikk sikkerhetsbegrensning ved henting av naermeste ledere for person", e)
            throw ForbiddenException()
        } catch (e: RuntimeException) {
            log.error("Fikk Runtimefeil ved henting av naermesteledere for person", e)
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NaermesteLederConsumer::class.java)
    }
}
