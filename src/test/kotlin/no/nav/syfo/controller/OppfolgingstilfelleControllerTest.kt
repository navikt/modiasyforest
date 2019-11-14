package no.nav.syfo.controller

import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus.*
import java.text.ParseException
import java.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class OppfolgingstilfelleControllerTest : AbstractControllerTilgangTest() {

    @Inject
    private lateinit var oppfolgingstilfelleController: OppfolgingstilfelleController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @Test
    fun getOppfolgingstilfellerHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val oppfolgingstilfelleList = oppfolgingstilfelleController.getOppfolgingstilfeller(ARBEIDSTAKER_FNR)
        assertThat(oppfolgingstilfelleList).hasSize(1)

        val oppfolgingstilfelle = oppfolgingstilfelleList[0]
        assertThat(oppfolgingstilfelle.sykmeldinger).hasSize(1)
        assertThat(oppfolgingstilfelle.sluttdato).isEqualTo(LocalDate.now().plusDays(30))
    }

    @Test(expected = ForbiddenException::class)
    fun getOppfolgingstilfellerAccessForbidden() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        oppfolgingstilfelleController.getOppfolgingstilfeller(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun getOppfolgingstilfellerAccessServerError() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        oppfolgingstilfelleController.getOppfolgingstilfeller(ARBEIDSTAKER_FNR)
    }
}
