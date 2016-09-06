package no.nav.sbl.dialogarena.modiasyforest.config;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/test")
@Produces(APPLICATION_JSON)
public class TestRessurs {

    @GET
    @Path("/tekster")
    public String hentTekster() {
        return "Kom hit";
    }
}
