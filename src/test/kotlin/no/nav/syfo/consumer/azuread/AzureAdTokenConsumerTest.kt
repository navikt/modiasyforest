package no.nav.syfo.consumer.azuread

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer.Companion.SYSTEM_TOKEN_EXPIRATION_MARGIN_SECONDS
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenResponse
import no.nav.syfo.metric.Metrikk
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class AzureAdTokenConsumerTest {
    @Inject
    private lateinit var restTemplateMedProxy: RestTemplate

    @Inject
    private lateinit var metrikk: Metrikk

    @Value("\${azure.openid.config.token.endpoint}")
    private lateinit var azureTokenEndpoint: String

    private lateinit var azureAdTokenConsumer: AzureAdV2TokenConsumer

    private lateinit var mockRestServiceServer: MockRestServiceServer

    @Before
    fun setup() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplateMedProxy).build()
        azureAdTokenConsumer = AzureAdV2TokenConsumer(
            restTemplateWithProxy = restTemplateMedProxy,
            azureAppClientId = "1345678",
            azureAppClientSecret = "secret",
            azureTokenEndpoint = azureTokenEndpoint,
        )
    }

    @After
    fun tearDown() {
        mockRestServiceServer.verify()
    }

    @Test
    fun `get token from azure if token is missing`() {
        val tokenGyldig = "token"
        val responseBody = azureAdResponseAsJsonString(tokenGyldig)
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON))

        val token = azureAdTokenConsumer.getSystemToken("test")

        assertThat(token).isEqualTo(tokenGyldig)
    }

    @Test
    fun `get token from azure if token is expired`() {
        val tokenUtlopt = "token_utlopt"
        val responseBodyUtlopt = azureAdResponseAsJsonString(
            token = tokenUtlopt,
            expires_in = SYSTEM_TOKEN_EXPIRATION_MARGIN_SECONDS - 1,
        )

        val tokenGyldig = "token_gyldig"
        val responseBodyGyldig = azureAdResponseAsJsonString(
            token = tokenGyldig,
            expires_in = SYSTEM_TOKEN_EXPIRATION_MARGIN_SECONDS,
        )

        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBodyUtlopt, MediaType.APPLICATION_JSON))
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBodyGyldig, MediaType.APPLICATION_JSON))

        azureAdTokenConsumer.getSystemToken("test")
        val token = azureAdTokenConsumer.getSystemToken("test")

        assertThat(token).isEqualTo(tokenGyldig)
    }

    @Test
    fun `use existing token if still valid`() {
        val tokenGyldig = "token_gyldig"
        val responseBodyGyldig = azureAdResponseAsJsonString(tokenGyldig)

        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBodyGyldig, MediaType.APPLICATION_JSON))

        azureAdTokenConsumer.getSystemToken("test")
        val token = azureAdTokenConsumer.getSystemToken("test")

        assertThat(token).isEqualTo(tokenGyldig)
    }

    @Test
    fun `get token from azure if there is no token for the given resource`() {
        val tokenForResource1 = "token_1"
        val resource1 = "resource_1"
        val responseBody1 = azureAdResponseAsJsonString(tokenForResource1)

        val tokenForResource2 = "token_2"
        val resource2 = "resource_2"
        val responseBody2 = azureAdResponseAsJsonString(tokenForResource2)

        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBody1, MediaType.APPLICATION_JSON))
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(azureTokenEndpoint)).andRespond(MockRestResponseCreators.withSuccess(responseBody2, MediaType.APPLICATION_JSON))

        val token1 = azureAdTokenConsumer.getSystemToken(resource1)
        val token2 = azureAdTokenConsumer.getSystemToken(resource2)
        val token3 = azureAdTokenConsumer.getSystemToken(resource2)

        assertThat(token1).isEqualTo(token1)
        assertThat(token2).isEqualTo(token2)
        assertThat(token3).isEqualTo(token2)
    }

    private fun azureAdResponseAsJsonString(
        token: String,
        expires_in: Long = 3600L,
    ): String {
        val objectMapper = ObjectMapper()
        val module = JavaTimeModule()
        objectMapper.registerModule(module)
        val azureAdResponse = AzureAdV2TokenResponse(
            access_token = token,
            expires_in = expires_in
        )
        return try {
            objectMapper.writeValueAsString(azureAdResponse)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
