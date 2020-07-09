package no.nav.syfo.oidc

import no.nav.security.oidc.OIDCConstants
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.security.oidc.context.OIDCValidationContext
import no.nav.syfo.services.ws.OnBehalfOfOutInterceptor
import org.apache.cxf.endpoint.Client

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: OIDCRequestContextHolder, issuer: String?): String {
        val context = contextHolder
            .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT) as OIDCValidationContext
        return context.getToken(issuer).idToken
    }

    @JvmStatic
    fun leggTilOnBehalfOfOutInterceptorForOIDC(client: Client, OIDCToken: String?) {
        client.requestContext[OnBehalfOfOutInterceptor.REQUEST_CONTEXT_ONBEHALFOF_TOKEN_TYPE] = OnBehalfOfOutInterceptor.TokenType.OIDC
        client.requestContext[OnBehalfOfOutInterceptor.REQUEST_CONTEXT_ONBEHALFOF_TOKEN] = OIDCToken
    }
}
