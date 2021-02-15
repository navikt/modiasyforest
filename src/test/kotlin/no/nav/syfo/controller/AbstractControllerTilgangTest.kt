package no.nav.syfo.controller

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
abstract class AbstractControllerTilgangTest {
    @Value("\${tilgangskontrollapi.url}")
    private lateinit var tilgangskontrollUrl: String

    @Inject
    lateinit var tokenValidationContextHolder: TokenValidationContextHolder

    @Inject
    private lateinit var restTemplate: RestTemplate
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Before
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build()
    }

    @After
    open fun tearDown() {
        mockRestServiceServer.verify()
        loggUtAlle(tokenValidationContextHolder)
        mockRestServiceServer.reset()
    }

    fun mockSvarFraTilgangTilBrukerViaAzure(fnr: String, status: HttpStatus) {
        val uriString = UriComponentsBuilder.fromHttpUrl(tilgangskontrollUrl)
            .path(TilgangConsumer.TILGANG_TIL_BRUKER_VIA_AZURE_PATH)
            .queryParam(TilgangConsumer.FNR, fnr)
            .toUriString()
        val idToken = tokenValidationContextHolder.tokenValidationContext.getJwtToken(OIDCIssuer.AZURE).tokenAsString
        mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(uriString))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer $idToken"))
            .andRespond(MockRestResponseCreators.withStatus(status))
    }
}
