package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.NO_BIGIP_5XX_REDIRECT;

@Provider
public class SyfoException extends RuntimeException implements ExceptionMapper<SyfoException> {

    public Feil feil;

    @SuppressWarnings("unused")
    public SyfoException() {
    }

    public SyfoException(Feil feil) {
        this.feil = feil;
    }

    @Override
    public Response toResponse(SyfoException e) {
        Feilmelding melding = new Feilmelding().withFeil(e.feil);

        return Response
                .status(e.status())
                .entity(melding)
                .type(APPLICATION_JSON)
                .header(NO_BIGIP_5XX_REDIRECT, true)
                .build();
    }

    private int status() {
        return this.feil.status.getStatusCode();
    }
}
