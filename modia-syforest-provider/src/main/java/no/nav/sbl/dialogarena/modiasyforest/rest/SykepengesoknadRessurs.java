package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Count;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykepengesoknaderService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.metrics.MetricsFactory.createEvent;

@Controller
@Path("/sykepengesoknader")
@Produces(APPLICATION_JSON)
public class SykepengesoknadRessurs {

    @Inject
    private SykepengesoknaderService sykepengesoknaderService;
    @Inject
    private AktoerService aktoerService;

    @GET
    @Count(name = "hentSykepengesoknader")
    public List<Sykepengesoknad> hentSykepengesoknader(@QueryParam("fnr") String fnr){
        try {
            return sykepengesoknaderService.hentSykepengesoknader(aktoerService.hentAktoerIdForFnr(fnr));
        } catch (ForbiddenException e) {
            createEvent("hentSykepengesoknader.403").report();
            throw e;
        }
    }
}
