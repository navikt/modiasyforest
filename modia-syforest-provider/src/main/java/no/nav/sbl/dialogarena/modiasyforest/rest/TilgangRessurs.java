package no.nav.sbl.dialogarena.modiasyforest.rest;

import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

@Controller
@Path("/toggle/tilgangmoteadmin")
public class TilgangRessurs {

    @GET
    public Boolean harSaksbehandlerTilgang() {
        String veildere = getProperty("tilgang.mote");
        return veildere != null && asList(veildere.split(",")).stream()
                .filter(s -> s.equals(getSubjectHandler().getUid()))
                .findAny()
                .isPresent();
    }
}
