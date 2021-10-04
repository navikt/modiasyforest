package no.nav.syfo.consumer

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.getConsumerClientIdFraOIDC
import no.nav.syfo.api.auth.OIDCUtil.getNAVIdentFraOIDC
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.util.*
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
    private val tilgangskontrollPersonUrl: String

    init {
        tilgangskontrollPersonUrl = "$tilgangskontrollUrl$TILGANGSKONTROLL_PERSON_PATH"
    }

    fun throwExceptionIfVeilederWithoutAccessOBO(fnr: String) {
        val harTilgang = isVeilederGrantedAccessToUserWithADOBO(fnr)
        if (!harTilgang) {
            throw ForbiddenException()
        }
    }

    private fun isVeilederGrantedAccessToUserWithADOBO(fnr: String): Boolean {
        val token = tokenFraOIDC(tokenValidationContextHolder, OIDCIssuer.INTERN_AZUREAD_V2)
        val veilederId = getNAVIdentFraOIDC(tokenValidationContextHolder)
            ?: throw RuntimeException("Missing veilderid from oidc-token")
        val azp = getConsumerClientIdFraOIDC(tokenValidationContextHolder)
            ?: throw RuntimeException("Missing azp from oidc-token")
        val oboToken = azureAdV2TokenConsumer.getOnBehalfOfToken(
            scopeClientId = syfotilgangskontrollClientId,
            token = token,
            veilederId = veilederId,
            azp = azp,
        )
        return try {
            template.exchange(
                tilgangskontrollPersonUrl,
                HttpMethod.GET,
                createEntity(token = oboToken, personIdentNumber = fnr),
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

    private fun createEntity(
        personIdentNumber: String,
        token: String
    ): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        headers[NAV_PERSONIDENT_HEADER] = personIdentNumber
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        return HttpEntity(headers)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TilgangConsumer::class.java)

        const val TILGANGSKONTROLL_PERSON_PATH = "/navident/person"
    }
}
