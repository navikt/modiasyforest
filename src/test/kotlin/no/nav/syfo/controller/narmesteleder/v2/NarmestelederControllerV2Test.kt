package no.nav.syfo.controller.narmesteleder.v2

import no.nav.syfo.consumer.narmesteleder.NarmesteLederService
import no.nav.syfo.controller.AbstractControllerTilgangTest
import no.nav.syfo.controller.narmesteleder.NaermesteLeder
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederADV2
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.LEDER_AKTORID
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class NarmestelederControllerV2Test : AbstractControllerTilgangTest() {

    @MockBean
    private lateinit var narmesteLederService: NarmesteLederService

    @Inject
    private lateinit var narmestelederControllerV2: NarmestelederControllerV2

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederADV2(tokenValidationContextHolder, VEILEDER_ID)
    }

    @After
    override fun tearDown() {
        loggUtAlle(tokenValidationContextHolder)
    }

    @Test
    fun `getAllNarmesteledere returns list of NarmesteLederRelasjon if veileder has access and PersonIdent is present in request param`() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, OK)

        val expectedNaermesteledere = listOf(NaermesteLeder().withAktoerId(LEDER_AKTORID))

        `when`(narmesteLederService.narmesteLedere(Fodselsnummer(ARBEIDSTAKER_FNR))).thenReturn(expectedNaermesteledere)

        val actualNaermesteLedere = narmestelederControllerV2.getAllNarmesteledere(
            ARBEIDSTAKER_FNR,
            LinkedMultiValueMap()
        )

        assertThat(actualNaermesteLedere.size).isEqualTo(expectedNaermesteledere.size)
        assertThat(actualNaermesteLedere.first().aktoerId).isEqualTo(expectedNaermesteledere.first().aktoerId)
    }

    @Test
    fun `getAllNarmesteledere returns list of NarmesteLederRelasjon if veileder has access and PersonIdent is present in request header`() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, OK)

        val expectedNaermesteledere = listOf(NaermesteLeder().withAktoerId(LEDER_AKTORID))

        `when`(narmesteLederService.narmesteLedere(Fodselsnummer(ARBEIDSTAKER_FNR))).thenReturn(expectedNaermesteledere)

        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_FNR)

        val actualNaermesteLedere = narmestelederControllerV2.getAllNarmesteledere(
            null,
            headers
        )

        assertThat(actualNaermesteLedere.size).isEqualTo(expectedNaermesteledere.size)
        assertThat(actualNaermesteLedere.first().aktoerId).isEqualTo(expectedNaermesteledere.first().aktoerId)
    }

    @Test(expected = ForbiddenException::class)
    fun `getAllNarmesteledere throws exception if no access`() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, FORBIDDEN)

        narmestelederControllerV2.getAllNarmesteledere(
            ARBEIDSTAKER_FNR,
            LinkedMultiValueMap()
        )
    }

    @Test(expected = RuntimeException::class)
    fun `getAllNarmesteledere throws exception if Server Error from tilgang`() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        narmestelederControllerV2.getAllNarmesteledere(
            ARBEIDSTAKER_FNR,
            LinkedMultiValueMap()
        )
    }
}
