package no.nav.sbl.dialogarena.modiasyforest.controller;

import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/diskresjonskode/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class DiskresjonskodeController {

    private DiskresjonskodeService diskresjonskodeService;

    @Inject
    public DiskresjonskodeController(
            final DiskresjonskodeService diskresjonskodeService
    ) {
        this.diskresjonskodeService = diskresjonskodeService;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public DiskresjonskodeSvar hentDiskresjonskode(@PathVariable String fnr) {
        return new DiskresjonskodeSvar(diskresjonskodeService.diskresjonskode(fnr));
    }

    public class DiskresjonskodeSvar {
        public String diskresjonskode;

        public DiskresjonskodeSvar(String diskresjonskode) {
            this.diskresjonskode = diskresjonskode;
        }
    }
}
