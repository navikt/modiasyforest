package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederRessurs {

    @Inject
    private NaermesteLederService naermesteLederService;

    @GET
    @Timed
    public List<Naermesteleder> hentNaermesteledere(@QueryParam("fnr") String fnr) {
        return naermesteLederService.hentNaermesteledere(fnr);
    }
}
