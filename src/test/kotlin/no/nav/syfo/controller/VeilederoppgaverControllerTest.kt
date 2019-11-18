package no.nav.syfo.controller

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.services.TilgangService
import no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.util.basicCredentials
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doThrow
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl
import java.text.ParseException
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class VeilederoppgaverControllerTest {

    @Value("\${syfoveilederoppgaver.system.v1.url}")
    private lateinit var veilederoppgaverUrl: String

    @Value("\${syfoveilederoppgaver.systemapi.username}")
    private lateinit var apiUsername: String

    @Value("\${syfoveilederoppgaver.systemapi.password}")
    private lateinit var apiPassword: String

    @Inject
    private lateinit var oidcRequestContextHolder: OIDCRequestContextHolder

    @MockBean
    private lateinit var tilgangService: TilgangService

    @Inject
    private lateinit var veilederoppgaverController: VeilederoppgaverController

    @Inject
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockRestServiceServerVeilederOppgaver: MockRestServiceServer

    @Before
    @Throws(ParseException::class)
    fun setup() {
        this.mockRestServiceServerVeilederOppgaver = MockRestServiceServer.bindTo(restTemplate).build()
        logInVeilederAD(oidcRequestContextHolder, UserConstants.VEILEDER_ID)
    }

    @Test
    fun getVeilederoppgaver() {
        `when`(tilgangService.isVeilederGrantedAccessToUserWithAD(ARBEIDSTAKER_FNR)).thenReturn(true)

        val jsonValue = ""

        mockVeilederoppgaver(ARBEIDSTAKER_FNR, jsonValue)

        val veilederoppgaveList = veilederoppgaverController.getVeilederoppgaver(ARBEIDSTAKER_FNR)

        assertThat(veilederoppgaveList).hasSize(0)
    }

    @Test(expected = ForbiddenException::class)
    fun getVeilederoppgaverNoAccess() {
        doThrow(ForbiddenException::class.java).`when`(tilgangService).throwExceptionIfVeilederWithoutAccess(ARBEIDSTAKER_FNR)

        veilederoppgaverController.getVeilederoppgaver(ARBEIDSTAKER_FNR)
    }

    @Test(expected = RuntimeException::class)
    fun getVeilederoppgaverAccessServerErrror() {
        doThrow(HttpServerErrorException.InternalServerError::class.java).`when`(tilgangService).throwExceptionIfVeilederWithoutAccess(ARBEIDSTAKER_FNR)

        veilederoppgaverController.getVeilederoppgaver(ARBEIDSTAKER_FNR)
    }

    private fun mockVeilederoppgaver(fnr: String, jsonValue: String) {
        val uriString = fromHttpUrl(veilederoppgaverUrl)
                .queryParam(TilgangService.FNR, fnr)
                .toUriString()

        mockRestServiceServerVeilederOppgaver.expect(once(), requestTo(uriString))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, basicCredentials(apiUsername, apiPassword)))
                .andRespond(withSuccess(jsonValue, MediaType.APPLICATION_JSON))
    }
}
