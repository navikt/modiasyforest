package no.nav.sbl.dialogarena.modiasyforest.rest.informasjon;


import no.nav.sbl.tekster.TeksterAPI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.tekster.Utils.convertResourceBundleToProperties;

@Path("/informasjon")
@Produces(APPLICATION_JSON)
public class TeksterRessurs {

    @Inject
    @Named("moteTekster")
    private TeksterAPI moteTekster;

    @Inject
    @Named("syfoTekster")
    private TeksterAPI syfoTekster;

    @GET
    @Path("/tekster")
    public Properties hentTekster() throws IOException {
        Properties ledetekster = new Properties();
        ledetekster.putAll(convertResourceBundleToProperties(syfoTekster.hentTekster("nb")));
        return ledetekster;
    }
}