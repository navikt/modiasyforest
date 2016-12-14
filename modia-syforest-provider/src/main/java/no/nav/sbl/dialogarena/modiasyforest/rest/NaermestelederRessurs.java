package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
@Path("/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederRessurs {
    private static final Logger LOG = getLogger(NaermestelederRessurs.class);

    @Inject
    private NaermesteLederService naermesteLederService;
    @Inject
    private SykmeldingService sykmeldingService;

    @GET
    @Timed
    public List<NaermesteLeder> hentNaermesteledere(@QueryParam("fnr") String fnr) throws HentNaermesteLederListeSikkerhetsbegrensning {
        List<Sykmelding> sykmeldinger = new ArrayList<>();
        try {
            sykmeldinger = sykmeldingService.hentSykmeldinger(fnr, asList(WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER));
        } catch (SyfoException e) {
            LOG.warn("Brukeren har ikke tilgang til sykmeldingene, men vi viser nærmeste leder allikevel.");
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
