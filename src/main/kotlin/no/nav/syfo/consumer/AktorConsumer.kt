package no.nav.syfo.consumer

import no.nav.syfo.config.CacheConfig
import no.nav.syfo.log
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@Service
class AktorConsumer @Inject
constructor(private val aktoerV2: AktoerV2) {

    val log = log()

    @Cacheable(cacheNames = [CacheConfig.CACHENAME_AKTORID], key = "#fnr", condition = "#fnr != null")
    fun hentAktoerIdForFnr(fnr: String): String {
        if (StringUtils.isBlank(fnr) || !fnr.matches("\\d{11}$".toRegex())) {
            log.error("Prøvde å hente aktoerId med fnr")
            throw IllegalArgumentException()
        }

        try {
            return aktoerV2.hentAktoerIdForIdent(
                    WSHentAktoerIdForIdentRequest()
                            .withIdent(fnr)
            ).aktoerId
        } catch (e: HentAktoerIdForIdentPersonIkkeFunnet) {
            log.warn("AktoerID ikke funnet for fødselsnummer", e)
            throw NotFoundException()
        }

    }

    @Cacheable(cacheNames = [CacheConfig.CACHENAME_AKTORFNR], key = "#aktoerId", condition = "#aktoerId != null")
    fun hentFnrForAktoer(aktoerId: String): String {
        if (StringUtils.isBlank(aktoerId) || !aktoerId.matches("\\d{13}$".toRegex())) {
            log.error("Prøvde å hente fnr med aktoerId {}", aktoerId)
            throw IllegalArgumentException()
        }

        try {
            return aktoerV2.hentIdentForAktoerId(
                    WSHentIdentForAktoerIdRequest()
                            .withAktoerId(aktoerId)
            ).ident
        } catch (e: HentIdentForAktoerIdPersonIkkeFunnet) {
            log.error("Fnr ikke funnet for aktoerId {}!", aktoerId, e)
            throw NotFoundException()
        }

    }
}

