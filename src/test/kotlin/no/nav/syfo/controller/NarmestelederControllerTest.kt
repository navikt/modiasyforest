package no.nav.syfo.controller

import no.nav.syfo.consumer.narmesteleder.NaermesteLeder
import no.nav.syfo.consumer.narmesteleder.NarmesteLederService
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.LEDER_AKTORID
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.*
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class NarmestelederControllerTest : AbstractControllerTilgangTest() {

    @MockBean
    private lateinit var narmesteLederService: NarmesteLederService

    @Inject
    private lateinit var narmestelederController: NarmestelederController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @After
    override fun tearDown() {
        loggUtAlle(oidcRequestContextHolder)
    }

    @Test
    fun `getAllNarmesteledere returns list of NarmesteLederRelasjon if veileder has access`() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val expectedNaermesteledere = listOf(NaermesteLeder().withAktoerId(LEDER_AKTORID))

        `when`(narmesteLederService.narmesteLedere(Fodselsnummer(ARBEIDSTAKER_FNR))).thenReturn(expectedNaermesteledere)

        val actualNaermesteLedere = narmestelederController.getAllNarmesteledere(ARBEIDSTAKER_FNR)

        assertThat(actualNaermesteLedere.size).isEqualTo(expectedNaermesteledere.size)
        assertThat(actualNaermesteLedere[0].aktoerId).isEqualTo(expectedNaermesteledere[0].aktoerId)
    }

    @Test(expected = ForbiddenException::class)
    fun `getAllNarmesteledere throws exception if no access`() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        narmestelederController.getAllNarmesteledere(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun `getAllNarmesteledere throws exception if Server Error from tilgang`() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        narmestelederController.getAllNarmesteledere(ARBEIDSTAKER_FNR)
    }
}
