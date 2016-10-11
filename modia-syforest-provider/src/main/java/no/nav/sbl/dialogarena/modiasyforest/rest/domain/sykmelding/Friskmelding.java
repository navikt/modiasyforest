package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;


import java.time.LocalDate;

public class Friskmelding {
    public Boolean arbeidsfoerEtterPerioden;
    public String hensynPaaArbeidsplassen;
    public boolean antarReturSammeArbeidsgiver;
    public LocalDate antattDatoReturSammeArbeidsgiver;
    public boolean antarReturAnnenArbeidsgiver;
    public LocalDate tilbakemeldingReturArbeid;
    public boolean utenArbeidsgiverAntarTilbakeIArbeid;
    public LocalDate utenArbeidsgiverAntarTilbakeIArbeidDato;
    public LocalDate utenArbeidsgiverTilbakemelding;
}
