package no.nav.syfo.controller

import no.nav.syfo.controller.domain.sykepengesoknad.Sykepengesoknad
import no.nav.syfo.mocks.SykepengesoeknadV1Mock.wsSykepengesoknadListe
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

class SykepengesoknadADControllerTest : AbstractControllerTilgangTest() {

    @Inject
    private lateinit var sykepengesoknadController: SykepengesoknadADController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID)
    }

    @Test
    fun getSykepengesoknaderHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK)

        val sykepengesoknadListe: List<Sykepengesoknad> = sykepengesoknadController.getSykepengesoknader(ARBEIDSTAKER_FNR)

        assertThat(sykepengesoknadListe).hasSize(wsSykepengesoknadListe.size)
    }

    @Test(expected = ForbiddenException::class)
    fun getSykepengesoknaderAccessForbidden() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN)

        sykepengesoknadController.getSykepengesoknader(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun getSykepengesoknaderAccessServerError() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR)

        sykepengesoknadController.getSykepengesoknader(ARBEIDSTAKER_FNR)
    }
}
