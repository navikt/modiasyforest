package no.nav.syfo.controller

import no.nav.syfo.LocalApplication
import no.nav.syfo.mocks.OppfoelgingMock.Companion.OPPFOLGINGSTILFELLE_PERIODE_AKTIVITET
import no.nav.syfo.mocks.OppfoelgingMock.Companion.OPPFOLGINGSTILFELLE_PERIODE_FOM
import no.nav.syfo.mocks.OppfoelgingMock.Companion.OPPFOLGINGSTILFELLE_PERIODE_GRAD
import no.nav.syfo.mocks.OppfoelgingMock.Companion.OPPFOLGINGSTILFELLE_PERIODE_TOM
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
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

    @Inject
    private lateinit var oppfolgingstilfelleperioderController: OppfolgingstilfelleperioderADController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @Test
    fun getOppfolgingstilfelleperioder() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val oppfolgingstilfelleList = oppfolgingstilfelleperioderController.getOppfolgingstilfelleperioder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER)

        assertThat(oppfolgingstilfelleList).hasSize(1)

        val oppfolgingstilfelle = oppfolgingstilfelleList[0]
        assertThat(oppfolgingstilfelle.aktivitet).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_AKTIVITET)
        assertThat(oppfolgingstilfelle.grad).isEqualTo(OPPFOLGINGSTILFELLE_PERIODE_GRAD)
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
