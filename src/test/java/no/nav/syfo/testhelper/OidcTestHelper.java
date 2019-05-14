package no.nav.syfo.testhelper;

import com.nimbusds.jwt.SignedJWT;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.security.spring.oidc.test.JwtTokenGenerator;

public class OidcTestHelper {

    public static void loggInnVeileder(OIDCRequestContextHolder oidcRequestContextHolder, String subject) {
        //OIDC-hack - legg til token og oidcclaims for en test-person
        SignedJWT jwt = JwtTokenGenerator.createSignedJWT(subject);
        String issuer = OIDCIssuer.INTERN;
        TokenContext tokenContext = new TokenContext(issuer, jwt.serialize());
        OIDCClaims oidcClaims = new OIDCClaims(jwt);
        OIDCValidationContext oidcValidationContext = new OIDCValidationContext();
        oidcValidationContext.addValidatedToken(issuer, tokenContext, oidcClaims);
        oidcRequestContextHolder.setOIDCValidationContext(oidcValidationContext);
    }

    public static void loggUtAlle(OIDCRequestContextHolder oidcRequestContextHolder) {
        oidcRequestContextHolder.setOIDCValidationContext(null);
    }

}
