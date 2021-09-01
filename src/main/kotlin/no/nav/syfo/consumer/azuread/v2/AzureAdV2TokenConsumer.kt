package no.nav.syfo.consumer.azuread.v2

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.*

@Component
class AzureAdV2TokenConsumer @Autowired constructor(
    @Qualifier("restTemplateWithProxy") private val restTemplateWithProxy: RestTemplate,
    @Value("\${azure.app.client.id}") private val azureAppClientId: String,
    @Value("\${azure.app.client.secret}") private val azureAppClientSecret: String,
    @Value("\${azure.openid.config.token.endpoint}") private val azureTokenEndpoint: String,
) {
    private val azureAdTokenMap: MutableMap<String, AzureAdV2Token> = HashMap()

    fun getSystemToken(
        scopeClientId: String,
    ): String {
        val expirationWithMargin = LocalDateTime.now().plusSeconds(SYSTEM_TOKEN_EXPIRATION_MARGIN_SECONDS)
        val azureAdResponse = azureAdTokenMap[scopeClientId]
        if (azureAdResponse == null || azureAdResponse.expires.isBefore(expirationWithMargin)) {
            try {
                val entity = systemTokenRequestEntity(
                    scopeClientId = scopeClientId,
                )
                val azureAdV2Token = getToken(entity = entity)
                azureAdTokenMap[scopeClientId] = azureAdV2Token
                return azureAdV2Token.accessToken
            } catch (e: RestClientResponseException) {
                log.error("Call to get AzureADV2Token from AzureAD for scope: $scopeClientId with status: ${e.rawStatusCode} and message: ${e.responseBodyAsString}", e)
                throw e
            }
        }
        return azureAdTokenMap[scopeClientId]!!.accessToken
    }

    fun getOnBehalfOfToken(
        scopeClientId: String,
        token: String,
    ): String {
        try {
            val entity = onBehalfOfRequestEntity(
                scopeClientId = scopeClientId,
                token = token
            )
            return getToken(entity = entity).accessToken
        } catch (e: RestClientResponseException) {
            log.error("Call to get AzureADV2Token from AzureAD for scope: $scopeClientId with status: ${e.rawStatusCode} and message: ${e.responseBodyAsString}", e)
            throw e
        }
    }

    fun getToken(
        entity: HttpEntity<MultiValueMap<String, String>>,
    ): AzureAdV2Token {
        val response = restTemplateWithProxy.exchange(
            azureTokenEndpoint,
            HttpMethod.POST,
            entity,
            AzureAdV2TokenResponse::class.java
        )
        val tokenResponse = response.body!!

        return tokenResponse.toAzureAdV2Token()
    }

    private fun onBehalfOfRequestEntity(
        scopeClientId: String,
        token: String,
    ): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", azureAppClientId)
        body.add("client_secret", azureAppClientSecret)
        body.add("client_assertion_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
        body.add("assertion", token)
        body.add("scope", "api://$scopeClientId/.default")
        body.add("requested_token_use", "on_behalf_of")
        return HttpEntity<MultiValueMap<String, String>>(body, headers)
    }

    private fun systemTokenRequestEntity(
        scopeClientId: String,
    ): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", azureAppClientId)
        body.add("scope", "api://$scopeClientId/.default")
        body.add("grant_type", "client_credentials")
        body.add("client_secret", azureAppClientSecret)

        return HttpEntity<MultiValueMap<String, String>>(body, headers)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AzureAdV2TokenConsumer::class.java)
        const val SYSTEM_TOKEN_EXPIRATION_MARGIN_SECONDS = 120L
    }
}
