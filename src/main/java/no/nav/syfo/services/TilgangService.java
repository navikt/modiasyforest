package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.oidc.OIDCUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.ForbiddenException;
import java.net.URI;
import java.util.Collections;

import static java.util.Collections.singletonMap;
import static no.nav.syfo.util.CredentialUtilKt.bearerCredentials;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class TilgangService {

    private final OIDCRequestContextHolder oidcContextHolder;

    public static final String FNR = "fnr";
    public static final String TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker";
    private static final String FNR_PLACEHOLDER = "{" + FNR + "}";
    private final RestTemplate template;
    private final UriComponentsBuilder tilgangTilBrukerViaAzureUriTemplate;

    public TilgangService(
            OIDCRequestContextHolder oidcContextHolder,
            @Value("${tilgangskontrollapi.url}") String tilgangskontrollUrl,
            RestTemplate template
    ) {
        this.oidcContextHolder = oidcContextHolder;
        tilgangTilBrukerViaAzureUriTemplate = fromHttpUrl(tilgangskontrollUrl)
                .path(TILGANG_TIL_BRUKER_VIA_AZURE_PATH)
                .queryParam(FNR, FNR_PLACEHOLDER);
        this.template = template;
    }

    public void throwExceptionIfVeilederWithoutAccess(String fnr) {
        boolean harTilgang = isVeilederGrantedAccessToUserWithAD(fnr);
        if (!harTilgang) {
            throw new ForbiddenException();
        }
    }

    public boolean isVeilederGrantedAccessToUserWithAD(String fnr) {
        URI tilgangTilBrukerViaAzureUriMedFnr = tilgangTilBrukerViaAzureUriTemplate.build(singletonMap(FNR, fnr));
        return checkAccess(tilgangTilBrukerViaAzureUriMedFnr, OIDCIssuer.AZURE);
    }

    private boolean checkAccess(URI uri, String oidcIssuer) {
        try {
            template.exchange(
                    uri,
                    HttpMethod.GET,
                    createEntity(oidcIssuer),
                    String.class
            );
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 403) {
                return false;
            } else {
                throw e;
            }
        }
    }

    private HttpEntity<String> createEntity(String issuer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.AUTHORIZATION, bearerCredentials(OIDCUtil.tokenFraOIDC(oidcContextHolder, issuer)));
        return new HttpEntity<>(headers);
    }
}
