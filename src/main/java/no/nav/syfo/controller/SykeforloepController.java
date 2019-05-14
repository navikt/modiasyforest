package no.nav.syfo.controller;

import no.nav.syfo.controller.domain.Sykeforloep;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.SykeforloepService;
import no.nav.syfo.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/sykeforloep")
@Produces(APPLICATION_JSON)
public class SykeforloepController {

    private SykeforloepService sykeforloepService;
    private TilgangService tilgangService;

    @Inject
    public SykeforloepController(
            SykeforloepService sykeforloepService,
            TilgangService tilgangService
    ) {
        this.sykeforloepService = sykeforloepService;
        this.tilgangService = tilgangService;
    }

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
}
