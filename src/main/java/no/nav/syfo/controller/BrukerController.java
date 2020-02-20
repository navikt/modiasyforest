package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.*;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/brukerinfo")
@Produces(APPLICATION_JSON)
public class BrukerController {

    private BrukerprofilService brukerprofilService;
    private DkifService dkifService;
    private TilgangService tilgangsKontroll;

    @Inject
    public BrukerController(
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

        return brukerprofilService.hentBruker(fnr)
                .kontaktinfo(dkifService.hentKontaktinfoFnr(fnr, OIDCIssuer.INTERN))
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
