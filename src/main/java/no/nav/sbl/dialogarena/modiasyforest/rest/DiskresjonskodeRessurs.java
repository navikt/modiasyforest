package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer.INTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/diskresjonskode/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class DiskresjonskodeRessurs {

    private DiskresjonskodeService diskresjonskodeService;

    @Inject
    public DiskresjonskodeRessurs(
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

    @ExceptionHandler({IllegalArgumentException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(BAD_REQUEST.value(), "Vi kunne ikke tolke inndataene :/");
    }
}
