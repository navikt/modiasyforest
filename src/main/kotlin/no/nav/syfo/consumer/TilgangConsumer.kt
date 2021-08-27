package no.nav.syfo.consumer

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.*
import javax.ws.rs.ForbiddenException

@Service
class TilgangConsumer(
    @Value("\${tilgangskontrollapi.url}") private val tilgangskontrollUrl: String,
    @Value("\${syfotilgangskontroll.client.id}") private val syfotilgangskontrollClientId: String,
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    private val template: RestTemplate
) {
    fun throwExceptionIfVeilederWithoutAccessOBO(fnr: String) {
        val harTilgang = isVeilederGrantedAccessToUserWithADOBO(fnr)
        if (!harTilgang) {
            throw ForbiddenException()
        }
    }

    private fun isVeilederGrantedAccessToUserWithADOBO(fnr: String): Boolean {
        val token = tokenFraOIDC(tokenValidationContextHolder, OIDCIssuer.INTERN_AZUREAD_V2)
        val oboToken = azureAdV2TokenConsumer.getOnBehalfOfToken(
            scopeClientId = syfotilgangskontrollClientId,
            token = token
        )

        val url = accessToUserV2Url(fnr)

        return try {
            template.exchange(
                url,
                HttpMethod.GET,
                createEntity(token = oboToken),
                String::class.java
            )
            true
        } catch (e: HttpClientErrorException) {
            if (e.rawStatusCode == 403) {
                false
            } else {
                log.error("HttpClientErrorException mot tilgangskontroll", e)
                throw e
            }
        } catch (e: HttpServerErrorException) {
            log.error("HttpServerErrorException mot tilgangskontroll med status ${e.rawStatusCode}", e)
            return false
        }
    }

    fun accessToUserV2Url(personIdentNumber: String): String {
        return "$tilgangskontrollUrl$TILGANG_TIL_BRUKER_VIA_AZURE_V2_PATH/$personIdentNumber"
    }

    private fun createEntity(token: String): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        return HttpEntity(headers)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TilgangConsumer::class.java)

        const val TILGANG_TIL_BRUKER_VIA_AZURE_V2_PATH = "/navident/bruker"
    }
}
