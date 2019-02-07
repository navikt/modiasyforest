package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.sbl.dialogarena.modiasyforest.services.*;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.BrukerMapper.ws2bruker;
import static no.nav.sbl.dialogarena.modiasyforest.mock.brukerMock.brukerMedAdresser;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.map;

@Controller
@Path("/brukerinfo")
@Produces(APPLICATION_JSON)
public class BrukerRessurs {

    @Inject
    private BrukerprofilService brukerprofilService;
    @Inject
    private DkifService dkifService;
    @Inject
    private TilgangService tilgangsKontroll;

    @GET
    @Timed
    public Bruker hentNavn(@QueryParam("fnr") String fnr) {
        tilgangsKontroll.sjekkTilgangTilPerson(fnr);

        if ("true".equals(getProperty("local.mock"))) {
            return brukerMedAdresser();
        }
        return map(brukerprofilService.hentBruker(fnr), ws2bruker)
                .kontaktinfo(dkifService.hentKontaktinfoFnr(fnr))
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
