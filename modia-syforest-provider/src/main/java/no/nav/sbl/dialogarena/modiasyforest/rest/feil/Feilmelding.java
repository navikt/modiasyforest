package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.*;

public class Feilmelding {

    static final String NO_BIGIP_5XX_REDIRECT = "X-Escape-5xx-Redirect";

    public enum Feil {
        GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.generell.feil"
        ),
        IKKE_FOEDSELSNUMMER (
                INTERNAL_SERVER_ERROR, "feilmelding.ikke.fnr"
        ),
        INGEN_AKTOER_ID (
                INTERNAL_SERVER_ERROR, "feilmelding.ingen.aktoer.id"
        ),
        AKTOER_IKKE_FUNNET (
                INTERNAL_SERVER_ERROR, "feilmelding.aktoer.ikke.funnet"
        ),
        ARBEIDSFORHOLD_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidsgivere.generell.feil"
        ),
        ARBEIDSFORHOLD_UGYLDIG_INPUT (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidsgivere.ugyldig.input"
        ),
        ARBEIDSFORHOLD_INGEN_TILGANG (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidsgivere.sikkerhetsbegrensning"
        ),
        SYKMELDING_INGEN_TILGANG(
                FORBIDDEN, "feilmelding.sykmelding.ingen.tilgang"
        ),
        SYKEFORLOEP_INGEN_TILGANG(
                FORBIDDEN, "feilmelding.sykeforloep.ingen.tilgang"
        ),
        SEND_SYKMELDING_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.send.sykmelding.generell.feil"
        ),
        TPS_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.tps.generell.feil"
        ),
        ORGANISASJON_IKKE_FUNNET (
                NOT_FOUND, "feilmelding.organisasjon.ikke.funnet"
        ),
        ORGANISASJON_UGYLDIG_INPUT (
                INTERNAL_SERVER_ERROR, "feilmelding.organisasjon.ugyldig.input"
        ),
        ORGANISASJON_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.organisasjon.generell.feil"
        ),
        SYKMELDING_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.sykmelding.generell.feil"
        ),
        SYKMELDING_IKKE_FUNNET (
                NOT_FOUND, "feilmelding.sykmelding.ikke.funnet"
        );

        public Status status;
        public String id;
        Feil(Status status, String id) {
            this.status = status;
            this.id = id;
        }
    }

    private Feil feil;

    Feilmelding withFeil(final Feil feil) {
        this.feil = feil;
        return this;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return feil.id;
    }
}
