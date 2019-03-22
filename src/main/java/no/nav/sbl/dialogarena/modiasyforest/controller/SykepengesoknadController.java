package no.nav.sbl.dialogarena.modiasyforest.controller;

import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykepengesoknaderService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.sbl.dialogarena.modiasyforest.utils.Metrikk;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/sykepengesoknader")
@Produces(APPLICATION_JSON)
public class SykepengesoknadController {

    private Metrikk metrikk;
    private AktoerService aktoerService;
    private SykepengesoknaderService sykepengesoknaderService;
    private TilgangService tilgangService;

    @Inject
    public SykepengesoknadController(
            Metrikk metrikk,
            AktoerService aktoerService,
            SykepengesoknaderService sykepengesoknaderService,
            TilgangService tilgangService
    ) {
        this.metrikk = metrikk;
        this.aktoerService = aktoerService;
        this.sykepengesoknaderService = sykepengesoknaderService;
        this.tilgangService = tilgangService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykepengesoknad> hentSykepengesoknader(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellEndepunktKall("hent_sykepengesoknader");
        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        try {
            return sykepengesoknaderService.hentSykepengesoknader(aktoerService.hentAktoerIdForFnr(fnr), OIDCIssuer.INTERN);
        } catch (ForbiddenException e) {
            metrikk.tellHentSykepengesoknader403();
            throw e;
        }
    }
}
