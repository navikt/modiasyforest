package no.nav.sbl.dialogarena.modiasyforest.controller;

import no.nav.sbl.dialogarena.modiasyforest.controller.domain.Oppfolgingstilfelle;
import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/oppfolgingstilfelleperioder")
@Produces(APPLICATION_JSON)
public class OppfolgingstilfelleperioderController {

    private TilgangService tilgangsKontroll;
    private SykeforloepService sykeforloepService;

    @Inject
    public OppfolgingstilfelleperioderController(
            final TilgangService tilgangsKontroll,
            final SykeforloepService sykeforloepService
    ) {
        this.tilgangsKontroll = tilgangsKontroll;
        this.sykeforloepService = sykeforloepService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Oppfolgingstilfelle> hentOppfolgingstilfelleperioder(
            @RequestParam(value = "fnr") String fnr,
            @RequestParam(value = "orgnummer") String orgnummer
    ) {
        tilgangsKontroll.sjekkVeiledersTilgangTilPerson(fnr);

        return sykeforloepService.hentOppfolgingstilfelleperioder(fnr, orgnummer);
    }
}
