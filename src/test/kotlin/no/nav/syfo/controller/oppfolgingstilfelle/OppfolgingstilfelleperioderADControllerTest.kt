package no.nav.syfo.controller.oppfolgingstilfelle

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.controller.AbstractControllerTilgangTest
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
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
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class OppfolgingstilfelleperioderADControllerTest : AbstractControllerTilgangTest() {

    @Value("\${pdl.url}")
    private lateinit var pdlUrl: String

    @Value("\${syfosyketilfelle.url}")
    private lateinit var syketilfelleUrl: String

    @MockBean
    private lateinit var stsConsumer: StsConsumer

    @Inject
    private lateinit var oppfolgingstilfelleperioderController: OppfolgingstilfelleperioderADController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @Test
    fun getOppfolgingstilfelleperioder() {
        Mockito.`when`(stsConsumer.token()).thenReturn(STS_TOKEN)

        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        mockAndExpectPdlIdenterRequest(mockRestServiceServer, pdlUrl)

        mockAndExpectSyketilfelleRequest(
            mockRestServiceServer,
            "$syketilfelleUrl/kafka/oppfolgingstilfelle/beregn/$ARBEIDSTAKER_AKTORID/$VIRKSOMHETSNUMMER"
        )

        val oppfolgingstilfelleList = oppfolgingstilfelleperioderController.getOppfolgingstilfelleperioder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER)

        assertThat(oppfolgingstilfelleList).hasSize(1)

        val oppfolgingstilfelle = oppfolgingstilfelleList.first()
        assertThat(oppfolgingstilfelle.orgnummer).isEqualTo(VIRKSOMHETSNUMMER)
        assertThat(oppfolgingstilfelle.fom).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_FOM)
        assertThat(oppfolgingstilfelle.tom).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_TOM)
    }

    @Test(expected = ForbiddenException::class)
    fun getOppfolgingstilfelleperioderNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        oppfolgingstilfelleperioderController.getOppfolgingstilfelleperioder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER)
    }

    @Test(expected = RuntimeException::class)
    fun getOppfolgingstilfelleperioderAccessServerErrror() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        oppfolgingstilfelleperioderController.getOppfolgingstilfelleperioder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER)
    }
}
