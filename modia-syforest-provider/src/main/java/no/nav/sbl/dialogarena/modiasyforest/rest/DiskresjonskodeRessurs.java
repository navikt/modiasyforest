package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/diskresjonskode/{fnr}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class DiskresjonskodeRessurs {

    @Inject
    private TilgangService tilgangService;

    @Inject
    private DiskresjonskodeService diskresjonskodeService;

    @GET
    public DiskresjonskodeSvar hentDiskresjonskode(@PathParam("fnr") String fnr) {
        tilgangService.sjekkTilgangTilPerson(fnr);
        return new DiskresjonskodeSvar(diskresjonskodeService.diskresjonskode(fnr));
    }

    public class DiskresjonskodeSvar {
        public String diskresjonskode;
        public DiskresjonskodeSvar(String diskresjonskode) {
            this.diskresjonskode = diskresjonskode;
        }
    }
}
