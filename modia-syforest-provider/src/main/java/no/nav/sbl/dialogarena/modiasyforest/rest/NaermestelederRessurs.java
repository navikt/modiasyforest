package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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
    public List<Naermesteleder> hentNaermesteledere(@QueryParam("fnr") String fnr) {
        List<Sykmelding> sykmeldinger = sykmeldingService.hentSykmeldinger(fnr, asList(WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER));
        List<Naermesteleder> naermesteledere = naermesteLederService.hentNaermesteledere(fnr);
        naermesteledere.addAll(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(naermesteledere, sykmeldinger));
        int idcounter = 0;
        for (Naermesteleder naermesteleder : naermesteledere) {
            naermesteleder.id = idcounter++;
        }
        return naermesteledere;
    }
}
