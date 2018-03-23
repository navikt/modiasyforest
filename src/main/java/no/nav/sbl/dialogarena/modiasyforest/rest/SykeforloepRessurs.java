package no.nav.sbl.dialogarena.modiasyforest.rest;


import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/sykeforloep")
@Produces(APPLICATION_JSON)
public class SykeforloepRessurs {

    @Inject
    private TilgangService tilgangService;
    @Inject
    private SykeforloepService sykeforloepService;

    @GET
    @Timed
    public List<Sykeforloep> hentOppfoelgingstilfeller(@QueryParam("fnr") String fnr){
        tilgangService.sjekkTilgangTilPerson(fnr);

        if ("true".equals(System.getProperty("test.local"))) {
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
        return sykeforloepService.hentSykeforloep(fnr);
    }
}
