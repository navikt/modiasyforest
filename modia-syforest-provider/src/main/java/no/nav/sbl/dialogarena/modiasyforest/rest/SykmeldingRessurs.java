package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.metrics.aspects.Count;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.sbl.dialogarena.modiasyforest.mock.sykmeldingMock.sykmeldingerMock;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER;

@Controller
@Path("/sykmeldinger")
@Produces(APPLICATION_JSON)
public class SykmeldingRessurs {

    @Inject
    private SykmeldingService sykmeldingService;

    @GET
    @Count(name = "hentSykmeldinger")
    public List<Sykmelding> hentSykmeldinger(@QueryParam("fnr") String fnr, @QueryParam("type") String type) {
        if ("true".equals(getProperty("local.mock"))) {
            return sykmeldingerMock();
        }
        List<WSSkjermes> filter = new ArrayList<>();
        if ("arbeidsgiver".equals(type)) {
            filter.add(SKJERMES_FOR_ARBEIDSGIVER);
        }
        try {
            return sykmeldingService.hentSykmeldinger(fnr, filter);
        } catch (SyfoException e) {
            if (e.feil.status.equals(FORBIDDEN)) {
                createEvent("hentSykmeldinger.403").report();
            }
            throw e;
        }
    }
}
