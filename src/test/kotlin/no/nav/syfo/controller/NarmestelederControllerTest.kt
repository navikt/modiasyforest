package no.nav.syfo.controller

import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.services.NaermesteLederService
import no.nav.syfo.services.SykmeldingService
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes
import org.junit.*
import org.mockito.ArgumentMatchers.anyListOf
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.*
import java.text.ParseException
import java.util.Collections.emptyList
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class NarmestelederControllerTest : AbstractControllerTilgangTest() {

    @MockBean
    private lateinit var sykmeldingService: SykmeldingService
    @MockBean
    private lateinit var naermesteLederService: NaermesteLederService

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
    fun getNarmesteledereHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        `when`(sykmeldingService.hentSykmeldinger(anyString(), anyListOf(WSSkjermes::class.java), anyString())).thenReturn(emptyList())
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
}
