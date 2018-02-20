package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.brukerdialog.security.context.SubjectHandler;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Component
public class TilgangService {

    private Client client = newClient();

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public void sjekkTilgangTilPerson(String fnr) {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        Response response = client.target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();

        if (200 != response.getStatus()) {
            if (403 == response.getStatus()) {
                throw new ForbiddenException("Brukeren har ikke tilgang til denne personen");
            } else {
                throw new WebApplicationException(response);
            }
        }
    }

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public void sjekkTilgangTilTjenesten() {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        Response response = client.target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtiltjenesten")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();

        if (200 != response.getStatus()) {
            if (403 == response.getStatus()) {
                throw new ForbiddenException("Brukeren har ikke tilgang til denne tjenesten");
            } else {
                throw new WebApplicationException(response);
            }
        }
    }

}
