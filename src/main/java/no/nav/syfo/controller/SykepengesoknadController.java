package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.controller.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.SykepengesoknaderService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.utils.Metrikk;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/sykepengesoknader")
@Produces(APPLICATION_JSON)
public class SykepengesoknadController {

    private Metrikk metrikk;
    private AktorConsumer aktorConsumer;
    private SykepengesoknaderService sykepengesoknaderService;
    private TilgangService tilgangService;

    @Inject
    public SykepengesoknadController(
            Metrikk metrikk,
            AktorConsumer aktorConsumer,
            SykepengesoknaderService sykepengesoknaderService,
            TilgangService tilgangService
    ) {
        this.metrikk = metrikk;
        this.aktorConsumer = aktorConsumer;
        this.sykepengesoknaderService = sykepengesoknaderService;
        this.tilgangService = tilgangService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykepengesoknad> hentSykepengesoknader(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellEndepunktKall("hent_sykepengesoknader");
        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        try {
            return sykepengesoknaderService.hentSykepengesoknader(aktorConsumer.hentAktoerIdForFnr(fnr), OIDCIssuer.INTERN);
        } catch (ForbiddenException e) {
            metrikk.tellHentSykepengesoknader403();
            throw e;
        }
    }
}
