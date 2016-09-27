package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.brukerinfo.Bruker;
import no.nav.sbl.dialogarena.modiasyforest.services.BrukerprofilService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/brukerinfo")
@Produces(APPLICATION_JSON)
public class BrukerRessurs {

    @Inject
    private BrukerprofilService brukerprofilService;

    @GET
    @Timed
    public Bruker hentNavn(@QueryParam("fnr") String fnr){
        return new Bruker()
                .withNavn(brukerprofilService.hentNavn(fnr))
                .withArbeidssituasjon("ARBEIDSTAKER");
    }
}
