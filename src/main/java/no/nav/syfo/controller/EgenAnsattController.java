package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.services.EgenAnsattService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/egenansatt/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class EgenAnsattController {

    private EgenAnsattService egenAnsattService;

    @Inject
    public EgenAnsattController(EgenAnsattService egenAnsattService) {
        this.egenAnsattService = egenAnsattService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping
    public EgenAnsattSvar hentErEgenAnsatt(@PathVariable("fnr") String fnr) {
        return new EgenAnsattSvar(egenAnsattService.erEgenAnsatt(fnr));
    }

    public class EgenAnsattSvar {
        public boolean erEgenAnsatt;

        public EgenAnsattSvar(boolean erEgenAnsatt) {
            this.erEgenAnsatt = erEgenAnsatt;
        }
    }
}
