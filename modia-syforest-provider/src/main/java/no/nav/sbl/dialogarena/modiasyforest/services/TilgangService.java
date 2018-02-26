package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoTilgangException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoTilgangException.*;

@Component
public class TilgangService {

    private Client client = newClient();

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public void sjekkTilgangTilPerson(String fnr) {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        Response response = client.target(getProperty("syfo-tilgangskontroll.endpoint.url") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();

        if (200 != response.getStatus()) {
            if (403 == response.getStatus()) {
                Tilgang tilgang = response.readEntity(Tilgang.class);
                if(EGEN_ANSATT.name().equals(tilgang.ikkeTilgangGrunn)){
                    throw new SyfoTilgangException(EGENANSATT);
                } else if(KODE6.name().equals(tilgang.ikkeTilgangGrunn)){
                    throw new SyfoTilgangException(DISKRESJON);
                } else if(KODE7.name().equals(tilgang.ikkeTilgangGrunn)){
                    throw new SyfoTilgangException(DISKRESJON);
                } else if(SYFO.name().equals(tilgang.ikkeTilgangGrunn)){
                    throw new SyfoTilgangException(SENSITIV);
                } else {
                    throw new SyfoException(Feil.GENERELL_FEIL);
                }
            } else {
                throw new SyfoException(Feil.GENERELL_FEIL);
            }
        }
    }

}
