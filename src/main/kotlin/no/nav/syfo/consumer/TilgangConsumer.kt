package no.nav.syfo.consumer

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import no.nav.syfo.util.bearerCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*
import javax.ws.rs.ForbiddenException

@Service
class TilgangConsumer(
    @Value("\${tilgangskontrollapi.url}") tilgangskontrollUrl: String,
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    private val template: RestTemplate
) {
    private val tilgangTilBrukerViaAzureUriTemplate: UriComponentsBuilder

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
                createEntity(oidcIssuer),
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

    private fun createEntity(issuer: String): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers[HttpHeaders.AUTHORIZATION] = bearerCredentials(tokenFraOIDC(tokenValidationContextHolder, issuer))
        return HttpEntity(headers)
    }

    companion object {
        const val FNR = "fnr"
        const val TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker"
        private const val FNR_PLACEHOLDER = "{$FNR}"
    }

    init {
        tilgangTilBrukerViaAzureUriTemplate = UriComponentsBuilder.fromHttpUrl(tilgangskontrollUrl)
            .path(TILGANG_TIL_BRUKER_VIA_AZURE_PATH)
            .queryParam(FNR, FNR_PLACEHOLDER)
    }
}
