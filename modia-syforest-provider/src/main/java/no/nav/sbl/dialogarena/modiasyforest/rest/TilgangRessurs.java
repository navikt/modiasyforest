package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Tilgang;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/toggle/tilgangmoteadmin")
@Produces(APPLICATION_JSON)
public class TilgangRessurs {

    @GET
    public Tilgang harSaksbehandlerTilgang() {
        return new Tilgang().withTilgang(true);
    }
}
