package no.nav.sbl.dialogarena.modiasyforest.rest.informasjon;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/informasjon")
@Produces(APPLICATION_JSON)
public class TeksterRessurs {

    @GET
    @Path("/tekster")
    public Properties hentTekster() throws IOException {
        Properties tekster = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tekster/sykefravaer.properties");
        tekster.load(inputStream);

        return tekster;
    }
}