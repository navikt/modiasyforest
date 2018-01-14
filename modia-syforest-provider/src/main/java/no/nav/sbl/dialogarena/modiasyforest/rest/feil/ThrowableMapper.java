package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.GENERELL_FEIL;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.NO_BIGIP_5XX_REDIRECT;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        Feilmelding melding = new Feilmelding().withFeil(GENERELL_FEIL);

        if (statuskode(e) == 500) {
            createEvent("runtimeexception").report();
        }

        return Response.status(statuskode(e))
                .type(APPLICATION_JSON)
                .entity(melding)
                .header(NO_BIGIP_5XX_REDIRECT, true)
                .build();
    }

    private int statuskode(Throwable e) {
        if (e instanceof WebApplicationException) {
            return ((WebApplicationException) e).getResponse().getStatus();
        } else {
            return GENERELL_FEIL.status.getStatusCode();
        }
    }
}
