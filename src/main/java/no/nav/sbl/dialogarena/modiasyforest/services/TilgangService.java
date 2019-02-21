package no.nav.sbl.dialogarena.modiasyforest.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.ForbiddenException;
import java.net.URI;

import static java.util.Collections.singletonMap;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class TilgangService {

    public static final String FNR = "fnr";
    public static final String TILGANG_TIL_BRUKER_PATH = "/tilgangtilbruker";
    private static final String FNR_PLACEHOLDER = "{" + FNR + "}";
    private final RestTemplate template;
    private final UriComponentsBuilder tilgangTilBrukerUriTemplate;

    public TilgangService(
            @Value("${tilgangskontrollapi.url}") String tilgangskontrollUrl,
            RestTemplate template
    ) {
        tilgangTilBrukerUriTemplate = fromHttpUrl(tilgangskontrollUrl)
                .path(TILGANG_TIL_BRUKER_PATH)
                .queryParam(FNR, FNR_PLACEHOLDER);
        this.template = template;
    }

    @Cacheable("veiledertilgangperson")
    public void sjekkVeiledersTilgangTilPerson(String fnr) {
        URI tilgangTilBrukerUriMedFnr = tilgangTilBrukerUriTemplate.build(singletonMap(FNR, fnr));
        boolean harTilgang = kallUriMedTemplate(tilgangTilBrukerUriMedFnr);
        if (!harTilgang) {
            throw new ForbiddenException();
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
}
