package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContextHolder

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: TokenValidationContextHolder, issuer: String): String {
        val context = contextHolder.tokenValidationContext
        return context.getJwtToken(issuer).tokenAsString
    }

    fun getNAVIdentFraOIDC(contextHolder: TokenValidationContextHolder): String? {
        val context = contextHolder.tokenValidationContext
        return context.getClaims(OIDCIssuer.INTERN_AZUREAD_V2).getStringClaim(OIDCClaim.NAVIDENT)
    }

    fun getConsumerClientIdFraOIDC(contextHolder: TokenValidationContextHolder): String? {
        val context = contextHolder.tokenValidationContext
        return context.getClaims(OIDCIssuer.INTERN_AZUREAD_V2).getStringClaim(OIDCClaim.JWT_CLAIM_AZP)
    }
}
