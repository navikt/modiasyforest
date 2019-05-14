package no.nav.syfo.consumer

import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import javax.ws.rs.NotFoundException

@RunWith(MockitoJUnitRunner::class)
class AktorConsumerTest {

    @Mock
    internal var aktoerV2: AktoerV2? = null

    @InjectMocks
    private val aktorConsumer: AktorConsumer? = null

    @Test(expected = IllegalArgumentException::class)
    fun exceptionVedTomtFNr() {
        aktorConsumer!!.hentAktoerIdForFnr("")
    }

    @Test
    @Throws(HentAktoerIdForIdentPersonIkkeFunnet::class)
    fun hentAktoerId() {
        val request = WSHentAktoerIdForIdentRequest().withIdent("12345678901")
        `when`(aktoerV2!!.hentAktoerIdForIdent(request)).thenReturn(WSHentAktoerIdForIdentResponse().withAktoerId("1234567890112312"))

        val aktoerId = aktorConsumer!!.hentAktoerIdForFnr("12345678901")
        assertThat(aktoerId).isEqualTo("1234567890112312")
    }

    @Test(expected = NotFoundException::class)
    @Throws(HentAktoerIdForIdentPersonIkkeFunnet::class)
    fun exceptionVedIkkeFunnet() {
        val request = WSHentAktoerIdForIdentRequest().withIdent("12345678901")
        `when`(aktoerV2!!.hentAktoerIdForIdent(request)).thenThrow(HentAktoerIdForIdentPersonIkkeFunnet())

        aktorConsumer!!.hentAktoerIdForFnr("12345678901")
    }
}
