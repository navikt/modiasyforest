package no.nav.syfo.controller

import no.nav.syfo.consumer.AktorConsumer
import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.narmesteleder.NarmesteLederConsumer
import no.nav.syfo.narmesteleder.NarmesteLederRelasjon
import no.nav.syfo.services.NaermesteLederService
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.UserConstants.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.mockito.ArgumentMatchers.anyListOf
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.*
import java.text.ParseException
import java.time.LocalDate
import java.util.Collections.emptyList
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class NarmestelederControllerTest : AbstractControllerTilgangTest() {

    @MockBean
    private lateinit var aktorConsumer: AktorConsumer

    @MockBean
    private lateinit var naermesteLederService: NaermesteLederService

    @MockBean
    private lateinit var narmesteLederConsumer: NarmesteLederConsumer

    @Inject
    private lateinit var narmestelederController: NarmestelederController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
        `when`(aktorConsumer.hentAktoerIdForFnr(ARBEIDSTAKER_FNR)).thenReturn(ARBEIDSTAKER_AKTORID)
    }

    @After
    override fun tearDown() {
        loggUtAlle(oidcRequestContextHolder)
    }

    @Test
    fun getNarmesteledereHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        `when`(naermesteLederService.hentNaermesteledere(ARBEIDSTAKER_FNR)).thenReturn(emptyList())
        `when`(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(anyListOf(NaermesteLeder::class.java), anyListOf(Sykmelding::class.java))).thenReturn(emptyList())

        narmestelederController.getNarmesteledere(ARBEIDSTAKER_FNR)
    }

    @Test(expected = ForbiddenException::class)
    fun getNarmesteledereNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        narmestelederController.getNarmesteledere(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun getNarmesteledereAccessServerError() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        narmestelederController.getNarmesteledere(ARBEIDSTAKER_FNR)
    }

    @Test
    fun `getAllNarmesteledere returns list of NarmesteLederRelasjon if veileder has access`() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val expectedNaermesteledere = listOf(NaermesteLeder().withAktoerId(LEDER_AKTORID))

        `when`(naermesteLederService.finnNarmesteLedere(ARBEIDSTAKER_FNR)).thenReturn(expectedNaermesteledere)

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
