package no.nav.syfo.mock

import no.nav.syfo.config.consumer.AktorConfig.Companion.MOCK_KEY
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [MOCK_KEY], havingValue = "true")
class AktorMock : AktoerV2 {

    val AKTOER_ID_MOCK = "1234567893210"

    override fun ping() {}

    override fun hentAktoerIdForIdentListe(wsHentAktoerIdForIdentListeRequest: WSHentAktoerIdForIdentListeRequest): WSHentAktoerIdForIdentListeResponse {
        throw RuntimeException("Ikke implementert i mock. Se AktoerMock")
    }

    @Throws(HentAktoerIdForIdentPersonIkkeFunnet::class)
    override fun hentAktoerIdForIdent(wsHentAktoerIdForIdentRequest: WSHentAktoerIdForIdentRequest): WSHentAktoerIdForIdentResponse {
        return WSHentAktoerIdForIdentResponse()
                .withAktoerId(AKTOER_ID_MOCK)
    }

    override fun hentIdentForAktoerIdListe(wsHentIdentForAktoerIdListeRequest: WSHentIdentForAktoerIdListeRequest): WSHentIdentForAktoerIdListeResponse {
        throw RuntimeException("Ikke implementert i mock. Se AktoerMock")
    }

    @Throws(HentIdentForAktoerIdPersonIkkeFunnet::class)
    override fun hentIdentForAktoerId(wsHentIdentForAktoerIdRequest: WSHentIdentForAktoerIdRequest): WSHentIdentForAktoerIdResponse {
        throw RuntimeException("Ikke implementert i mock. Se AktoerMock")
    }

    companion object {
        private val MOCK_AKTORID_PREFIX = "10"

        fun mockAktorId(fnr: String): String {
            return MOCK_AKTORID_PREFIX + fnr
        }
    }
}
