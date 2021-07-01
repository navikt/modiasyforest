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
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*
import javax.ws.rs.ForbiddenException

@Service
class TilgangConsumer(
    @Value("\${tilgangskontrollapi.url}") private val tilgangskontrollUrl: String,
    @Value("\${syfotilgangskontroll.client.id}") private val syfotilgangskontrollClientId: String,
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    private val template: RestTemplate
) {
    private val tilgangTilBrukerViaAzureUriTemplate: UriComponentsBuilder

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

    fun throwExceptionIfVeilederWithoutAccess(fnr: String) {
        val harTilgang = isVeilederGrantedAccessToUserWithAD(fnr)
        if (!harTilgang) {
            throw ForbiddenException()
        }
    }

    private fun isVeilederGrantedAccessToUserWithAD(fnr: String): Boolean {
        val tilgangTilBrukerViaAzureUriMedFnr = tilgangTilBrukerViaAzureUriTemplate.build(Collections.singletonMap(FNR, fnr))
        return checkAccess(tilgangTilBrukerViaAzureUriMedFnr, OIDCIssuer.AZURE)
    }

    private fun checkAccess(uri: URI, oidcIssuer: String): Boolean {
        return try {
            template.exchange(
                uri,
                HttpMethod.GET,
                createEntity(token = tokenFraOIDC(tokenValidationContextHolder, oidcIssuer)),
                String::class.java
            )
            true
        } catch (e: HttpClientErrorException) {
            if (e.rawStatusCode == 403) {
                false
            } else {
                throw e
            }
        }
    }

    private fun createEntity(token: String): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        return HttpEntity(headers)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TilgangConsumer::class.java)

        const val FNR = "fnr"
        const val TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker"
        const val TILGANG_TIL_BRUKER_VIA_AZURE_V2_PATH = "/navident/bruker"
        private const val FNR_PLACEHOLDER = "{$FNR}"
    }

    init {
        tilgangTilBrukerViaAzureUriTemplate = UriComponentsBuilder.fromHttpUrl(tilgangskontrollUrl)
            .path(TILGANG_TIL_BRUKER_VIA_AZURE_PATH)
            .queryParam(FNR, FNR_PLACEHOLDER)
    }
}
