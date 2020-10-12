package no.nav.syfo.consumer

import no.nav.syfo.config.CacheConfig.Companion.CACHENAME_BRUKER
import no.nav.syfo.controller.user.domain.Bruker
import no.nav.syfo.controller.user.BrukerMapper.toBruker
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.annotation.*
import org.springframework.stereotype.Service
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.xml.ws.soap.SOAPFaultException

@Service
class BrukerprofilConsumer @Inject constructor(
    private val brukerprofilV3: BrukerprofilV3
) {
    @Retryable(value = [SOAPFaultException::class], backoff = Backoff(delay = 200, maxDelay = 1000))
    @Cacheable(cacheNames = [CACHENAME_BRUKER], key = "#fnr", condition = "#fnr != null")
    fun hentBruker(fnr: String): Bruker {
        if (!fnr.matches(Regex("\\d{11}$"))) {
            log.error("Prøvde å hente navn med fnr")
            throw RuntimeException()
        }
        return try {
            val wsBruker = brukerprofilV3.hentKontaktinformasjonOgPreferanser(WSHentKontaktinformasjonOgPreferanserRequest()
                .withIdent(WSNorskIdent()
                    .withIdent(fnr))).bruker as WSBruker
            wsBruker.toBruker()
        } catch (e: HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt med FNR", e)
            throw RuntimeException()
        } catch (e: HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning) {
            log.error("Sikkerhetsbegrensning med FNR", e)
            throw ForbiddenException()
        } catch (e: HentKontaktinformasjonOgPreferanserPersonIkkeFunnet) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet med FNR", e)
            throw RuntimeException()
        } catch (e: RuntimeException) {
            log.error("Fikk RuntimeException mot TPS med ved oppslag", e)
            throw e
        }
    }

    @Recover
    fun recover(e: SOAPFaultException) {
        log.error("Feil ved kall hentKontaktinfo for Ident etter maks antall kall", e)
        throw e
    }

    companion object {
        private val log = LoggerFactory.getLogger(BrukerprofilConsumer::class.java)
    }
}
