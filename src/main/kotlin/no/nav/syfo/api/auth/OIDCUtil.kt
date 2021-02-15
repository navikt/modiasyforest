package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContextHolder

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: TokenValidationContextHolder, issuer: String): String {
        val context = contextHolder.tokenValidationContext
        return context.getJwtToken(issuer).tokenAsString
    }
}
