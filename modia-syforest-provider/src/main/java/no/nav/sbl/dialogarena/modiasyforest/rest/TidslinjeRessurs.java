package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Tidslinje;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tidslinje")
@Produces(APPLICATION_JSON)
public class TidslinjeRessurs {

    @Inject
    private TidslinjeService tidslinjeService;

    @GET
    @Timed
    @Count(name = "hentTidslinje")
    public List<Tidslinje> hentTidslinje(@QueryParam("fnr") String fnr, @QueryParam("type") String type) {
        return tidslinjeService.hentTidslinjer(fnr, type);
    }
}


