package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
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

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static no.nav.metrics.MetricsFactory.createEvent;

@Controller
@Path("/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederRessurs {

    @Inject
    private NaermesteLederService naermesteLederService;
    @Inject
    private SykmeldingService sykmeldingService;

    @GET
    @Timed
    @Count(name = "hentNaermesteledere")
    public List<NaermesteLeder> hentNaermesteledere(@QueryParam("fnr") String fnr) {
        List<Sykmelding> sykmeldinger = new ArrayList<>();
        try {
            sykmeldinger = sykmeldingService.hentSykmeldinger(fnr, asList(WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER));
        } catch (SyfoException e) {
            if (e.feil.status.equals(FORBIDDEN)) {
                createEvent("hentNaermesteledere.403").report();
            }
        }
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentNaermesteledere(fnr);
        naermesteledere.addAll(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(naermesteledere, sykmeldinger));
        long idcounter = 0;
        for (NaermesteLeder naermesteleder : naermesteledere) {
            naermesteleder.id = idcounter++;
        }
        return naermesteledere;
    }
}
