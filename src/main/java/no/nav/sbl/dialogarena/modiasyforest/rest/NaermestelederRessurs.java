package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.sbl.dialogarena.modiasyforest.utils.Metrikk;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping(value = "/api/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederRessurs {

    private Metrikk metrikk;
    private NaermesteLederService naermesteLederService;
    private SykmeldingService sykmeldingService;
    private TilgangService tilgangService;

    @Inject
    public NaermestelederRessurs(
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

    @ExceptionHandler({IllegalArgumentException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(BAD_REQUEST.value(), "Vi kunne ikke tolke inndataene :/");
    }

    @ExceptionHandler({ForbiddenException.class})
    void handleForbiddenRequests(HttpServletResponse response) throws IOException {
        response.sendError(FORBIDDEN.value(), "Handling er forbudt");
    }
}
