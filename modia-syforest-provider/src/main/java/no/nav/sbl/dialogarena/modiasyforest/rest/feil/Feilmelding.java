package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

public class Feilmelding {
    static final String NO_BIGIP_5XX_REDIRECT = "X-Escape-5xx-Redirect";
    private Feil feil;

    Feilmelding withFeil(Feil feil) {
        this.feil = feil;
        return this;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return feil.id;
    }
}
