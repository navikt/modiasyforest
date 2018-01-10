package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/tilgang")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TilgangRessurs {

    @Inject
    private TilgangService tilgangService;

    @GET
    public boolean harTilgang() {
        return tilgangService.harTilgangTilTjenesten();
    }
}
