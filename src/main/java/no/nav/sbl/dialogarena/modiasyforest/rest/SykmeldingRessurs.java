package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.metrics.aspects.Count;
import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.mock.sykmeldingMock.sykmeldingerMock;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping(value = "/api/sykmeldinger")
@Produces(APPLICATION_JSON)
public class SykmeldingRessurs {

    @Inject
    private TilgangService tilgangService;
    @Inject
    private SykmeldingService sykmeldingService;

    @Count(name = "hentSykmeldinger")
    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public List<Sykmelding> hentSykmeldinger(
            @RequestParam(value = "fnr") String fnr,
            @RequestParam(value = "type", required = false) String type
    ) {
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

    @ExceptionHandler({IllegalArgumentException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(BAD_REQUEST.value(), "Vi kunne ikke tolke inndataene :/");
    }

    @ExceptionHandler({ForbiddenException.class})
    void handleForbiddenRequests(HttpServletResponse response) throws IOException {
        response.sendError(FORBIDDEN.value(), "Handling er forbudt");
    }
}
