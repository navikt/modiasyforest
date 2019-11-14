package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.Oppfolgingstilfelle;
import no.nav.syfo.services.OppfolgingstilfelleService;
import no.nav.syfo.services.TilgangService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/oppfolgingstilfelleperioder")
@Produces(APPLICATION_JSON)
public class OppfolgingstilfelleperioderController {

    private TilgangService tilgangsKontroll;
    private OppfolgingstilfelleService oppfolgingstilfelleService;

    @Inject
    public OppfolgingstilfelleperioderController(
            final TilgangService tilgangsKontroll,
            final OppfolgingstilfelleService oppfolgingstilfelleService
    ) {
        this.tilgangsKontroll = tilgangsKontroll;
        this.oppfolgingstilfelleService = oppfolgingstilfelleService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Oppfolgingstilfelle> hentOppfolgingstilfelleperioder(
            @RequestParam(value = "fnr") String fnr,
            @RequestParam(value = "orgnummer") String orgnummer
    ) {
        tilgangsKontroll.sjekkVeiledersTilgangTilPerson(fnr);

        return oppfolgingstilfelleService.hentOppfolgingstilfelleperioder(fnr, orgnummer);
    }
}
