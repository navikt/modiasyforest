package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.EgenAnsattService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;

@RestController
@RequestMapping(value = "/api/egenansatt/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class EgenAnsattRessurs {

    private EgenAnsattService egenAnsattService;

    @Inject
    public EgenAnsattRessurs(EgenAnsattService egenAnsattService) {
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
