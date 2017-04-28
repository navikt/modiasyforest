package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Tilgang;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.lang.System.getProperty;
import static java.util.Arrays.stream;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

@Controller
@Path("/toggle/tilgangmoteadmin")
@Produces(APPLICATION_JSON)
public class TilgangRessurs {

    @GET
    public Tilgang harSaksbehandlerTilgang() {
        String veildere = getProperty("tilgang.mote");
        return new Tilgang().withTilgang(veildere != null && stream(veildere.split(","))
                .anyMatch(s -> s.equals(getSubjectHandler().getUid())));
    }
}
