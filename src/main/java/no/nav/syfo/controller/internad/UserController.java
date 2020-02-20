package no.nav.syfo.controller.internad;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.services.*;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import static no.nav.syfo.oidc.OIDCIssuer.AZURE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = "/api/internad/brukerinfo")
public class UserController {

    private final BrukerprofilService brukerprofilService;
    private final DkifService dkifService;
    private final TilgangService tilgangsKontroll;

    @Inject
    public UserController(
            BrukerprofilService brukerprofilService,
            DkifService dkifService,
            TilgangService tilgangsKontroll
    ) {
        this.brukerprofilService = brukerprofilService;
        this.dkifService = dkifService;
        this.tilgangsKontroll = tilgangsKontroll;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Bruker getUser(@RequestParam(value = "fnr") String fnr) {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr);

        return brukerprofilService.hentBruker(fnr)
                .kontaktinfo(dkifService.hentKontaktinfoFnr(fnr, AZURE))
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
