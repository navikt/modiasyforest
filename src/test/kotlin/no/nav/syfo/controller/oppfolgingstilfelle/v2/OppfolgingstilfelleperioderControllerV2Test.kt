package no.nav.syfo.controller.oppfolgingstilfelle.v2

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.controller.AbstractControllerTilgangTest
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederADV2
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.STS_TOKEN
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class OppfolgingstilfelleperioderControllerV2Test : AbstractControllerTilgangTest() {

    @Value("\${pdl.url}")
    private lateinit var pdlUrl: String

    @Value("\${syfosyketilfelle.url}")
    private lateinit var syketilfelleUrl: String

    @MockBean
    private lateinit var stsConsumer: StsConsumer

    @Inject
    private lateinit var oppfolgingstilfelleperioderControllerV2: OppfolgingstilfelleperioderControllerV2

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederADV2(tokenValidationContextHolder, VEILEDER_ID)
    }

    @Test
    fun getOppfolgingstilfelleperioder() {
        Mockito.`when`(stsConsumer.token()).thenReturn(STS_TOKEN)

        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, OK)

        mockAndExpectPdlIdenterRequest(mockRestServiceServer, pdlUrl)

        mockAndExpectSyketilfelleRequest(
            mockRestServiceServer,
            "$syketilfelleUrl/kafka/oppfolgingstilfelle/beregn/$ARBEIDSTAKER_AKTORID/$VIRKSOMHETSNUMMER"
        )

        val oppfolgingstilfelleList = oppfolgingstilfelleperioderControllerV2.getOppfolgingstilfelleperioder(
            ARBEIDSTAKER_FNR,
            VIRKSOMHETSNUMMER,
            LinkedMultiValueMap()
        )

        assertThat(oppfolgingstilfelleList).hasSize(1)

        val oppfolgingstilfelle = oppfolgingstilfelleList.first()
        assertThat(oppfolgingstilfelle.orgnummer).isEqualTo(VIRKSOMHETSNUMMER)
        assertThat(oppfolgingstilfelle.fom).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_FOM)
        assertThat(oppfolgingstilfelle.tom).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_TOM)
    }

    @Test(expected = ForbiddenException::class)
    fun getOppfolgingstilfelleperioderNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, FORBIDDEN)

        oppfolgingstilfelleperioderControllerV2.getOppfolgingstilfelleperioder(
            ARBEIDSTAKER_FNR,
            VIRKSOMHETSNUMMER,
            LinkedMultiValueMap()
        )
    }

    @Test(expected = RuntimeException::class)
    fun getOppfolgingstilfelleperioderAccessServerErrror() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        oppfolgingstilfelleperioderControllerV2.getOppfolgingstilfelleperioder(
            ARBEIDSTAKER_FNR,
            VIRKSOMHETSNUMMER,
            LinkedMultiValueMap()
        )
    }
}
