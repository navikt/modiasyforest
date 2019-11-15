package no.nav.syfo.controller

import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus.*
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

class SykmeldingADControllerTest : AbstractControllerTilgangTest() {

    @Inject
    private lateinit var sykmeldingController: SykmeldingADController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @Test
    fun getSykmeldingerHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val sykmeldingList = sykmeldingController.getSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER)

        assertThat(sykmeldingList).isNotEmpty
    }

    @Test(expected = ForbiddenException::class)
    fun getSykmeldingerAccessForbidden() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        sykmeldingController.getSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER)
    }

    @Test(expected = RuntimeException::class)
    fun getSykmeldingerAccessServerError() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        sykmeldingController.getSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER)
    }

    companion object {

        private const val ARBEIDSGIVER = "arbeidsgiver"
    }
}
