package no.nav.syfo.controller.internad;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.services.DiskresjonskodeService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static no.nav.syfo.oidc.OIDCIssuer.AZURE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = "/api/internad/diskresjonskode/{fnr}")
public class DiskresjonskodeADController {

    private DiskresjonskodeService diskresjonskodeService;

    @Inject
    public DiskresjonskodeADController(
            final DiskresjonskodeService diskresjonskodeService
    ) {
        this.diskresjonskodeService = diskresjonskodeService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public DiskresjonskodeSvar getDiskresjonskode(@PathVariable String fnr) {
        return new DiskresjonskodeSvar(diskresjonskodeService.diskresjonskode(fnr));
    }

    public class DiskresjonskodeSvar {
        public String diskresjonskode;

        public DiskresjonskodeSvar(String diskresjonskode) {
            this.diskresjonskode = diskresjonskode;
        }
    }
}
