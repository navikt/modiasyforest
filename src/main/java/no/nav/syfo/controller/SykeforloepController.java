package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.Sykeforloep;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.SykeforloepService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.utils.Metrikk;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/sykeforloep")
@Produces(APPLICATION_JSON)
public class SykeforloepController {

    private Metrikk metrikk;
    private SykeforloepService sykeforloepService;
    private TilgangService tilgangService;

    @Inject
    public SykeforloepController(
            Metrikk metrikk,
            SykeforloepService sykeforloepService,
            TilgangService tilgangService
    ) {
        this.metrikk = metrikk;
        this.sykeforloepService = sykeforloepService;
        this.tilgangService = tilgangService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykeforloep> hentOppfoelgingstilfeller(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellEndepunktKall("hent_sykeforloep");

        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        return sykeforloepService.hentSykeforloep(fnr, OIDCIssuer.INTERN);
    }
}
