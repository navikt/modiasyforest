package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.sbl.dialogarena.modiasyforest.services.*;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;

import java.io.IOException;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.BrukerMapper.ws2bruker;
import static no.nav.sbl.dialogarena.modiasyforest.mock.brukerMock.brukerMedAdresser;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.map;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/brukerinfo")
@Produces(APPLICATION_JSON)
public class BrukerRessurs {

    private BrukerprofilService brukerprofilService;

    private DkifService dkifService;

    private TilgangService tilgangsKontroll;

    @Inject
    public BrukerRessurs(
            final BrukerprofilService brukerprofilService,
            final DkifService dkifService,
            final TilgangService tilgangsKontroll
    ) {
        this.brukerprofilService = brukerprofilService;
        this.dkifService = dkifService;
        this.tilgangsKontroll = tilgangsKontroll;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Bruker hentNavn(@RequestParam(value = "fnr") String fnr) {
        tilgangsKontroll.sjekkVeiledersTilgangTilPerson(fnr);

        if ("true".equals(getProperty("local.mock"))) {
            return brukerMedAdresser();
        }
        return map(brukerprofilService.hentBruker(fnr), ws2bruker)
                .kontaktinfo(dkifService.hentKontaktinfoFnr(fnr))
                .arbeidssituasjon("ARBEIDSTAKER");
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
