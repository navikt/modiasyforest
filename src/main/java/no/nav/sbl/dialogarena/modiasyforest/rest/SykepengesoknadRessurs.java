package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Count;
import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykepengesoknaderService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping(value = "/api/sykepengesoknader")
@Produces(APPLICATION_JSON)
public class SykepengesoknadRessurs {

    @Inject
    private TilgangService tilgangService;
    @Inject
    private SykepengesoknaderService sykepengesoknaderService;
    @Inject
    private AktoerService aktoerService;

    @Count(name = "hentSykepengesoknader")
    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykepengesoknad> hentSykepengesoknader(@RequestParam(value = "fnr") String fnr) {
        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        try {
            return sykepengesoknaderService.hentSykepengesoknader(aktoerService.hentAktoerIdForFnr(fnr), OIDCIssuer.INTERN);
        } catch (ForbiddenException e) {
            createEvent("hentSykepengesoknader.403").report();
            throw e;
        }
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
