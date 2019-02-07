package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Oppfolgingstilfelle;
import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/oppfolgingstilfelleperioder")
@Produces(APPLICATION_JSON)
public class OppfolgingstilfelleperioderRessurs {

    @Inject
    private TilgangService tilgangService;
    @Inject
    private SykeforloepService sykeforloepService;

    @GET
    public List<Oppfolgingstilfelle> hentOppfolgingstilfelle(
            @QueryParam("fnr") String fnr,
            @QueryParam("orgnummer") String orgnummer) {
        tilgangService.sjekkTilgangTilPerson(fnr);

        return sykeforloepService.hentOppfolgingstilfelle(fnr, orgnummer);
    }
}
