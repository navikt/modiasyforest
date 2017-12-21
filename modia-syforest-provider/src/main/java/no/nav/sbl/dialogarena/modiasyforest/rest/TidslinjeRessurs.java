package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Tidslinje;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.sbl.dialogarena.modiasyforest.mock.tidslinjeMock.tidslinjeMock;

@Controller
@Path("/tidslinje")
@Produces(APPLICATION_JSON)
public class TidslinjeRessurs {

    @Inject
    private TidslinjeService tidslinjeService;

    @GET
    @Timed
    @Count(name = "hentTidslinje")
    public List<Tidslinje> hentTidslinje(@QueryParam("fnr") String fnr, @QueryParam("type") String type) {
        if ("true".equals(getProperty("local.mock"))) {
            return tidslinjeMock(type);
        }
        try {
            return tidslinjeService.hentTidslinjer(fnr, type);
        } catch (SyfoException e) {
            if (e.feil.status.equals(FORBIDDEN)) {
                createEvent("hentTidslinje.403").report();
            }
            throw e;
        }
    }
}


