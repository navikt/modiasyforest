package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.common.auth.SsoToken.Type.OIDC;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;

@Component
public class TilgangService {
    private Client client = newClient();

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public void sjekkTilgangTilPerson(String fnr) {
        if ("true".equals(getProperty("tilgang.withmock"))) {
            return;
        }
        Response response = client.target(getProperty("TILGANGSKONTROLLAPI_URL") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + SubjectHandler.getSsoToken(OIDC).orElseThrow(IllegalArgumentException::new))
                .get();

        if (200 != response.getStatus()) {
            if (403 == response.getStatus()) {
                Tilgang tilgang = response.readEntity(Tilgang.class);
                if(EGEN_ANSATT.name().equals(tilgang.begrunnelse)){
                    throw new ForbiddenException("sykefravaer.veileder.feilmelding.EGENANSATT.melding");
                } else if(KODE6.name().equals(tilgang.begrunnelse)){
                    throw new ForbiddenException("sykefravaer.veileder.feilmelding.DISKRESJON.melding");
                } else if(KODE7.name().equals(tilgang.begrunnelse)){
                    throw new ForbiddenException("sykefravaer.veileder.feilmelding.DISKRESJON.melding");
                } else if(SYFO.name().equals(tilgang.begrunnelse)){
                    throw new ForbiddenException("sykefravaer.veileder.feilmelding.SENSITIV.melding");
                } else {
                    throw new ForbiddenException("feilmelding.generell.feil");
                }
            } else {
                throw new RuntimeException("feilmelding.generell.feil");
            }
        }
    }

}
