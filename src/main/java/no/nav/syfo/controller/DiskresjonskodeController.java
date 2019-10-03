package no.nav.syfo.controller;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.services.DiskresjonskodeService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;
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
