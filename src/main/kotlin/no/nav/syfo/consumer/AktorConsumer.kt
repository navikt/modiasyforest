package no.nav.syfo.consumer

import no.nav.syfo.config.CacheConfig
import no.nav.syfo.domain.AktorId
import no.nav.syfo.log
import no.nav.tjeneste.virksomhet.aktoer.v2.*
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@Service
class AktorConsumer @Inject constructor(
    private val aktoerV2: AktoerV2
) {
    val log = log()

    @Cacheable(cacheNames = ["aktoerid"], key = "#fnr", condition = "#fnr != null")
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

    @Cacheable(cacheNames = [CacheConfig.CACHENAME_AKTORFNR], key = "#aktorId.value", condition = "#aktorId.value != null")
    fun hentFnrForAktoer(aktorId: AktorId): String {
        try {
            return aktoerV2.hentIdentForAktoerId(
                WSHentIdentForAktoerIdRequest()
                    .withAktoerId(aktorId.value)
            ).ident
        } catch (e: HentIdentForAktoerIdPersonIkkeFunnet) {
            log.error("Fnr ikke funnet for aktorId {}!", aktorId.value, e)
            throw NotFoundException()
        }
    }
}

