package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykepengesoknaderService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/sykepengesoknader")
@Produces(APPLICATION_JSON)
public class SykepengesoknadRessurs {

    @Inject
    private SykepengesoknaderService sykepengesoknaderService;
    @Inject
    private AktoerService aktoerService;

    @GET
    @Timed
    public List<Sykepengesoknad> hentSykepengesoknader(@QueryParam("fnr") String fnr){
        return sykepengesoknaderService.hentSykepengesoknader(aktoerService.hentAktoerIdForIdent(fnr));
    }

}
