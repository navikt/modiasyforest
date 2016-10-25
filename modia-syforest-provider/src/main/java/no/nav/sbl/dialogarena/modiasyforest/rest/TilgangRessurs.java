package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Tilgang;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/toggle/tilgangmoteadmin")
@Produces(APPLICATION_JSON)
public class TilgangRessurs {

    @GET
    public Tilgang harSaksbehandlerTilgang() {
        String veildere = getProperty("tilgang.mote");
        return new Tilgang().withTilgang(veildere != null && asList(veildere.split(",")).stream()
                .filter(s -> s.equals(getSubjectHandler().getUid()))
                .findAny()
                .isPresent());
    }
}
