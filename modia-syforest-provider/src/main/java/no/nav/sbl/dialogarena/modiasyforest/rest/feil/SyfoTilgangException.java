package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.NO_BIGIP_5XX_REDIRECT;

@Provider
public class SyfoTilgangException  extends RuntimeException implements ExceptionMapper<SyfoTilgangException> {

    public static final String DISKRESJON = "DISKRESJON";
    public static final String GEOGRAFISK = "GEOGRAFISK";
    public static final String EGENANSATT = "EGENANSATT";
    public static final String SENSITIV = "SENSITIV";
    public String feilmelding;

    @SuppressWarnings("unused")
    public SyfoTilgangException() {
    }

    public SyfoTilgangException(String feilmelding) {
        this.feilmelding = feilmelding;
    }

    @Override
    public Response toResponse(SyfoTilgangException e) {
        Feilmelding feilmelding = new Feilmelding().withFeil(Feil.SYKEFORLOEP_INGEN_TILGANG);
        switch (e.feilmelding) {
            case DISKRESJON:
                feilmelding.withFeil(Feil.IKKE_TILGANG_DISKRESJON);
                break;
            case GEOGRAFISK:
                feilmelding.withFeil(Feil.IKKE_TILGANG_GEOGRAFISK);
                break;
            case EGENANSATT:
                feilmelding.withFeil(Feil.IKKE_TILGANG_EGENANSATT);
                break;
            case SENSITIV:
                feilmelding.withFeil(Feil.IKKE_TILGANG_SENSITIV);
                break;
        }
        return Response
                .status(403)
                .entity(feilmelding)
                .type(APPLICATION_JSON)
                .header(NO_BIGIP_5XX_REDIRECT, true)
                .build();
    }
}
