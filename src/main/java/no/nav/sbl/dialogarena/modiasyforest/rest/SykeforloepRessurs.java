package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping(value = "/api/sykeforloep")
@Produces(APPLICATION_JSON)
public class SykeforloepRessurs {

    @Inject
    private TilgangService tilgangService;
    @Inject
    private SykeforloepService sykeforloepService;

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykeforloep> hentOppfoelgingstilfeller(@RequestParam(value = "fnr") String fnr) {
        tilgangService.sjekkVeiledersTilgangTilPerson(fnr);

        if ("true".equals(System.getProperty("local.mock"))) {
            return Arrays.asList(
                    new Sykeforloep()
                            .withOppfolgingsdato(LocalDate.now().minusWeeks(5))
                            .withSluttdato(LocalDate.now().plusWeeks(5)),
                    new Sykeforloep()
                            .withOppfolgingsdato(LocalDate.now().minusWeeks(30))
                            .withSluttdato(LocalDate.now().minusWeeks(15)),
                    new Sykeforloep()
                            .withOppfolgingsdato(LocalDate.now().minusYears(1))
                            .withSluttdato(LocalDate.now().minusWeeks(40))
            );
        }
        return sykeforloepService.hentSykeforloep(fnr, OIDCIssuer.INTERN);
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
