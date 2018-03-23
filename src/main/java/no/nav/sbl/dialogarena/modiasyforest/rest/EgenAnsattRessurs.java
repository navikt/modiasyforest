package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.EgenAnsattService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/egenansatt/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class EgenAnsattRessurs {

    @Inject
    private TilgangService tilgangService;

    @Inject
    private EgenAnsattService egenAnsattService;

    @GET
    public EgenAnsattSvar hentErEgenAnsatt(@PathParam("fnr") String fnr) {
        tilgangService.sjekkTilgangTilPerson(fnr);
        return new EgenAnsattSvar(egenAnsattService.erEgenAnsatt(fnr));
    }

    public class EgenAnsattSvar {
        public boolean erEgenAnsatt;
        public EgenAnsattSvar(boolean erEgenAnsatt) {
            this.erEgenAnsatt = erEgenAnsatt;
        }
    }
}
