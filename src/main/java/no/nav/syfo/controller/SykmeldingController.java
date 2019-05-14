package no.nav.syfo.controller;


import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.SykmeldingService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.utils.Metrikk;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.mock.sykmeldingMock.sykmeldingerMock;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER;

@RestController
@RequestMapping(value = "/api/sykmeldinger")
@Produces(APPLICATION_JSON)
public class SykmeldingController {

    private Metrikk metrikk;
    private SykmeldingService sykmeldingService;
    private TilgangService tilgangService;

    @Inject
    public SykmeldingController(
            Metrikk metrikk,
            SykmeldingService sykmeldingService,
            TilgangService tilgangService
    ) {
        this.metrikk = metrikk;
        this.sykmeldingService = sykmeldingService;
        this.tilgangService = tilgangService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykmelding> hentSykmeldinger(
            @RequestParam(value = "fnr") String fnr,
            @RequestParam(value = "type", required = false) String type
    ) {
        metrikk.tellEndepunktKall("hent_sykmeldinger");

        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        if ("true".equals(getProperty("local.mock"))) {
            return sykmeldingerMock();
        }
        List<WSSkjermes> filter = new ArrayList<>();
        if ("arbeidsgiver".equals(type)) {
            filter.add(SKJERMES_FOR_ARBEIDSGIVER);
        }

        return sykmeldingService.hentSykmeldinger(fnr, filter, OIDCIssuer.INTERN);
    }
}
