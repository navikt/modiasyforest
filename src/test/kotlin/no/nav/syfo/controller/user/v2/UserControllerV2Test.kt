package no.nav.syfo.controller.user.v2

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.dkif.DkifConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.fullName
import no.nav.syfo.controller.AbstractControllerTilgangTest
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederADV2
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
import org.springframework.util.LinkedMultiValueMap
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class UserControllerV2Test : AbstractControllerTilgangTest() {
    @MockBean
    private lateinit var dkifConsumer: DkifConsumer

    @MockBean
    private lateinit var pdlConsumer: PdlConsumer

    @Inject
    private lateinit var userControllerV2: UserControllerV2

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
    fun userHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, HttpStatus.OK)
        val digitalKontaktinfo = generateDigitalKontaktinfo()
        val pdlResponse = generatePdlHentPerson(null)
        Mockito.`when`(dkifConsumer.kontaktinformasjon(ARBEIDSTAKER_FNR)).thenReturn(digitalKontaktinfo)
        Mockito.`when`(pdlConsumer.person(Fodselsnummer(ARBEIDSTAKER_FNR))).thenReturn(pdlResponse)
        val user = userControllerV2.getUser(ARBEIDSTAKER_FNR, LinkedMultiValueMap())
        Assert.assertEquals(pdlResponse.fullName(), user.navn)
    }

    @Test(expected = ForbiddenException::class)
    fun userNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, HttpStatus.FORBIDDEN)
        userControllerV2.getUser(ARBEIDSTAKER_FNR, LinkedMultiValueMap())
    }

    @Test(expected = RuntimeException::class)
    fun userServerErrror() {
        mockSvarFraTilgangTilBrukerViaAzureV2(ARBEIDSTAKER_FNR, HttpStatus.INTERNAL_SERVER_ERROR)
        userControllerV2.getUser(ARBEIDSTAKER_FNR, LinkedMultiValueMap())
    }
}
