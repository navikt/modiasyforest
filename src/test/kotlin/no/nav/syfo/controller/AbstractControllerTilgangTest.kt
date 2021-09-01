package no.nav.syfo.controller

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.TilgangConsumer.Companion.TILGANGSKONTROLL_PERSON_PATH
import no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle
import no.nav.syfo.testhelper.generateAzureAdV2TokenResponse
import no.nav.syfo.testhelper.mockAndExpectAzureADV2
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Qualifier
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

    @Value("\${azure.openid.config.token.endpoint}")
    private lateinit var azureTokenEndpoint: String

    @Value("\${tilgangskontrollapi.url}")
    private lateinit var tilgangskontrollUrl: String

    @Inject
    lateinit var tokenValidationContextHolder: TokenValidationContextHolder

    @Inject
    private lateinit var restTemplate: RestTemplate

    @Inject
    @Qualifier("restTemplateWithProxy")
    private lateinit var restTemplateWithProxy: RestTemplate

    lateinit var mockRestServiceServer: MockRestServiceServer
    lateinit var mockRestServiceWithProxyServer: MockRestServiceServer

    private val oboToken = "oboToken"

    @Before
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build()
        mockRestServiceWithProxyServer = MockRestServiceServer.bindTo(restTemplateWithProxy).build()
    }

    @After
    open fun tearDown() {
        mockRestServiceServer.verify()
        mockRestServiceWithProxyServer.verify()
        loggUtAlle(tokenValidationContextHolder)
        mockRestServiceServer.reset()
        mockRestServiceWithProxyServer.reset()
    }

    fun mockSvarFraTilgangTilBrukerViaAzureV2(fnr: String, status: HttpStatus) {
        mockAndExpectAzureADV2(
            mockRestServiceWithProxyServer,
            azureTokenEndpoint,
            generateAzureAdV2TokenResponse()
        )

        val uriString = UriComponentsBuilder.fromHttpUrl(tilgangskontrollUrl)
            .path(TILGANGSKONTROLL_PERSON_PATH)
            .toUriString()

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(uriString))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer ${generateAzureAdV2TokenResponse().access_token}"))
            .andExpect(MockRestRequestMatchers.header(NAV_PERSONIDENT_HEADER, fnr))
            .andRespond(MockRestResponseCreators.withStatus(status))
    }
}
