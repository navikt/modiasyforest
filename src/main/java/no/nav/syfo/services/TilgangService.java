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
import static no.nav.syfo.util.CredentialUtilKt.bearerHeader;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class TilgangService {

    private final OIDCRequestContextHolder oidcContextHolder;

    public static final String FNR = "fnr";
    public static final String TILGANG_TIL_BRUKER_PATH = "/tilgangtilbruker";
    public static final String TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker";
    public static final String ACCESS_TO_SYFO_WITH_AZURE_PATH = "/syfo";
    private static final String FNR_PLACEHOLDER = "{" + FNR + "}";
    private final RestTemplate template;
    private final UriComponentsBuilder tilgangTilBrukerUriTemplate;
    private final UriComponentsBuilder tilgangTilBrukerViaAzureUriTemplate;
    private final UriComponentsBuilder accessToSYFOWithAzureUriTemplate;

    public TilgangService(
            OIDCRequestContextHolder oidcContextHolder,
            @Value("${tilgangskontrollapi.url}") String tilgangskontrollUrl,
            RestTemplate template
    ) {
        this.oidcContextHolder = oidcContextHolder;
        tilgangTilBrukerUriTemplate = fromHttpUrl(tilgangskontrollUrl)
                .path(TILGANG_TIL_BRUKER_PATH)
                .queryParam(FNR, FNR_PLACEHOLDER);
        tilgangTilBrukerViaAzureUriTemplate = fromHttpUrl(tilgangskontrollUrl)
                .path(TILGANG_TIL_BRUKER_VIA_AZURE_PATH)
                .queryParam(FNR, FNR_PLACEHOLDER);
        accessToSYFOWithAzureUriTemplate = fromHttpUrl(tilgangskontrollUrl)
                .path(ACCESS_TO_SYFO_WITH_AZURE_PATH);
        this.template = template;
    }

    public void sjekkVeiledersTilgangTilPerson(String fnr) {
        URI tilgangTilBrukerUriMedFnr = tilgangTilBrukerUriTemplate.build(singletonMap(FNR, fnr));
        boolean harTilgang = kallUriMedTemplate(tilgangTilBrukerUriMedFnr);
        if (!harTilgang) {
            throw new ForbiddenException();
        }
    }

    public void throwExceptionIfVeilederWithoutAccess(String fnr) {
        boolean harTilgang = isVeilederGrantedAccessToUserWithAD(fnr);
        if (!harTilgang) {
            throw new ForbiddenException();
        }
    }

    public void throwExceptionIfVeilederWithoutAccessToSYFO() {
        if (!isVeilederGrantedAccessToSYFO()) {
            throw new ForbiddenException();
        }
    }

    public boolean isVeilederGrantedAccessToUserWithAD(String fnr) {
        URI tilgangTilBrukerViaAzureUriMedFnr = tilgangTilBrukerViaAzureUriTemplate.build(singletonMap(FNR, fnr));
        return checkAccess(tilgangTilBrukerViaAzureUriMedFnr, OIDCIssuer.AZURE);
    }

    public boolean isVeilederGrantedAccessToSYFO() {
        URI accessToSyfoUri = accessToSYFOWithAzureUriTemplate.build().toUri();
        return kallUriMedTemplate(accessToSyfoUri);
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

    private boolean kallUriMedTemplate(URI uri) {
        try {
            template.getForObject(uri, Object.class);
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
        headers.set("Authorization", bearerHeader(OIDCUtil.tokenFraOIDC(oidcContextHolder, issuer)));
        return new HttpEntity<>(headers);
    }
}
