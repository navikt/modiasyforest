package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.Sykeforloep;
import no.nav.syfo.services.SykeforloepService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/sykeforloep")
@Produces(APPLICATION_JSON)
public class SykeforloepRessurs {

    @Inject
    private SykeforloepService sykeforloepService;

    @GET
    @Timed
    public List<Sykeforloep> hentOppfoelgingstilfeller(@QueryParam("fnr") String fnr){
        return sykeforloepService.hentSykeforloep(fnr);
    }
}
