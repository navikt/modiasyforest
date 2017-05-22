package no.nav.sbl.dialogarena.modiasyforest.rest.feil;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

public enum Feil {
    GENERELL_FEIL (
            INTERNAL_SERVER_ERROR, "feilmelding.generell.feil"
    ),
    IKKE_TILGANG_GEOGRAFISK (
            FORBIDDEN, "sykefravaer.veileder.feilmelding.GEOGRAFISK.melding"
    ),
    IKKE_TILGANG_DISKRESJON(
            FORBIDDEN, "sykefravaer.veileder.feilmelding.DISKRESJON.melding"
    ),
    IKKE_TILGANG_EGENANSATT (
            FORBIDDEN, "sykefravaer.veileder.feilmelding.EGENANSATT.melding"
    ),
    IKKE_TILGANG_SENSITIV (
            FORBIDDEN, "sykefravaer.veileder.feilmelding.SENSITIV.melding"
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

    public Response.Status status;
    public String id;
    Feil(Response.Status status, String id) {
        this.status = status;
        this.id = id;
    }
}