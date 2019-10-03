package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.NaermesteLeder;
import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.*;
import no.nav.syfo.utils.Metrikk;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederController {

    private Metrikk metrikk;
    private NaermesteLederService naermesteLederService;
    private SykmeldingService sykmeldingService;
    private TilgangService tilgangService;

    @Inject
    public NaermestelederController(
            Metrikk metrikk,
            NaermesteLederService naermesteLederService,
            SykmeldingService sykmeldingService,
            TilgangService tilgangService
    ) {
        this.metrikk = metrikk;
        this.naermesteLederService = naermesteLederService;
        this.sykmeldingService = sykmeldingService;
        this.tilgangService = tilgangService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<NaermesteLeder> hentNaermesteledere(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellEndepunktKall("hent_naermesteledere");

        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        List<Sykmelding> sykmeldinger;
        try {
            sykmeldinger = sykmeldingService.hentSykmeldinger(fnr, singletonList(WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER), OIDCIssuer.INTERN);
        } catch (Exception e) {
            sykmeldinger = emptyList();
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
