package no.nav.syfo.controller.user

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.dkif.DkifConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.fullName
import no.nav.syfo.controller.AbstractControllerTilgangTest
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.VEILEDER_ID
import no.nav.syfo.testhelper.generateDigitalKontaktinfo
import no.nav.syfo.testhelper.generatePdlHentPerson
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class UserControllerTest : AbstractControllerTilgangTest() {
    @MockBean
    private lateinit var dkifConsumer: DkifConsumer

    @MockBean
    private lateinit var pdlConsumer: PdlConsumer

    @Inject
    private lateinit var userController: UserController

    @Before
    @Throws(ParseException::class)
    fun setup() {
        logInVeilederAD(tokenValidationContextHolder, VEILEDER_ID)
    }

    @After
    override fun tearDown() {
        loggUtAlle(tokenValidationContextHolder)
    }

    @Test
    fun userHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, HttpStatus.OK)
        val digitalKontaktinfo = generateDigitalKontaktinfo()
        val pdlResponse = generatePdlHentPerson(null)
        Mockito.`when`(dkifConsumer.kontaktinformasjon(ARBEIDSTAKER_FNR)).thenReturn(digitalKontaktinfo)
        Mockito.`when`(pdlConsumer.person(Fodselsnummer(ARBEIDSTAKER_FNR))).thenReturn(pdlResponse)
        val user = userController.getUser(ARBEIDSTAKER_FNR)
        Assert.assertEquals(pdlResponse.fullName(), user.navn)
    }

    @Test(expected = ForbiddenException::class)
    fun userNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, HttpStatus.FORBIDDEN)
        userController.getUser(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun userServerErrror() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, HttpStatus.INTERNAL_SERVER_ERROR)
        userController.getUser(ARBEIDSTAKER_FNR)
    }
}
