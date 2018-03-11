package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Sykmelding {

    public String id;
    public LocalDate startLegemeldtFravaer;
    public boolean skalViseSkravertFelt;
    public LocalDate identdato;
    public String status;

    // TODO: Disse bør legges i et eget objekt
    public String innsendtArbeidsgivernavn;
    public String valgtArbeidssituasjon;
    public String orgnummer;
    public LocalDateTime sendtdato;

    // 1 PASIENTOPPLYSNINGER
    public Pasient pasient = new Pasient();

    // 2 ARBEIDSGIVER
    public String arbeidsgiver;

    // 3 DIAGNOSE
    public Diagnoseinfo diagnose = new Diagnoseinfo();

    // 4 MULIGHET FOR ARBEID
    public MulighetForArbeid mulighetForArbeid = new MulighetForArbeid();

    // 5 FRISKMELDING
    public Friskmelding friskmelding = new Friskmelding();

    // 6 UTDYPENDE OPPLYSNINGER
    public UtdypendeOpplysninger utdypendeOpplysninger = new UtdypendeOpplysninger();

    // 7 HVA SKAL TIL FOR Å BEDRE ARBEIDSEVNEN
    public Arbeidsevne arbeidsevne = new Arbeidsevne();

    // 8 MELDING TIL NAV
    public MeldingTilNav meldingTilNav = new MeldingTilNav();

    // 9 MELDING TIL ARBEIDSGIVER
    public String innspillTilArbeidsgiver;

    // 11 TILBAKEDATERING
    public Tilbakedatering tilbakedatering = new Tilbakedatering();

    // 12 BEKREFTELSE
    public Bekreftelse bekreftelse = new Bekreftelse();

    public Sykmelding withId(final String id) {
        this.id = id;
        return this;
    }

    public Sykmelding withFnr(final String fnr) {
        this.pasient.fnr = fnr;
        return this;
    }

    public Sykmelding withFornavn(final String fornavn) {
        this.pasient.fornavn = fornavn;
        return this;
    }

    public Sykmelding withMellomnavn(final String mellomnavn) {
        this.pasient.mellomnavn = mellomnavn;
        return this;
    }
    public Sykmelding withEtternavn(final String etternavn) {
        this.pasient.etternavn = etternavn;
        return this;
    }

    public Sykmelding withSykmelder(final String sykmelder) {
        this.bekreftelse.sykmelder = sykmelder;
        return this;
    }

    public Sykmelding withSykmelderTlf(final String sykmelderTlf) {
        this.bekreftelse.sykmelderTlf = sykmelderTlf;
        return this;
    }

    public Sykmelding withUtstedelsesdato(final LocalDate utstedelsesdato) {
        this.bekreftelse.utstedelsesdato = utstedelsesdato;
        return this;
    }

    public Sykmelding withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public Sykmelding withInnsendtArbeidsgivernavn(final String innsendtArbeidsgivernavn) {
        this.innsendtArbeidsgivernavn = innsendtArbeidsgivernavn;
        return this;
    }

    public Sykmelding withValgtArbeidssituasjon(String valgtArbeidssituasjon) {
        this.valgtArbeidssituasjon = valgtArbeidssituasjon;
        return this;
    }

    public Sykmelding withArbeidsgiver(final String arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
        return this;
    }

    public Sykmelding withStatus(final String status) {
        this.status = status;
        return this;
    }

    public Sykmelding withHoveddiagnose(final Diagnose hoveddiagnose) {
        this.diagnose.hoveddiagnose = hoveddiagnose;
        return this;
    }

    public Sykmelding withBidiagnose(List<Diagnose> bidiagnoser) {
        this.diagnose.bidiagnoser = bidiagnoser;
        return this;
    }

    public Sykmelding withFravaersgrunnLovfestet(final String fravaersgrunnLovfestet) {
        this.diagnose.fravaersgrunnLovfestet = fravaersgrunnLovfestet;
        return this;
    }

    public Sykmelding withFravaerBeskrivelse(final String fravaerBeskrivelse) {
        this.diagnose.fravaerBeskrivelse = fravaerBeskrivelse;
        return this;
    }

    public Sykmelding withSvangerskap(final Boolean svangerskap) {
        this.diagnose.svangerskap = svangerskap;
        return this;
    }

    public Sykmelding withYrkesskade(final Boolean yrkesskade) {
        this.diagnose.yrkesskade = yrkesskade;
        return this;
    }

    public Sykmelding withYrkesskadeDato(final LocalDate yrkesskadeDato) {
        this.diagnose.yrkesskadeDato = yrkesskadeDato;
        return this;
    }

    public Sykmelding withSendtTilArbeidsgiverDato(final LocalDateTime sendtdato) {
        this.sendtdato = sendtdato;
        return this;
    }

    public Sykmelding withArbeidsfoerEtterPerioden(final Boolean arbeidsfoerEtterPerioden) {
        this.friskmelding.arbeidsfoerEtterPerioden = arbeidsfoerEtterPerioden;
        return this;
    }

    public Sykmelding withHensynPaaArbeidsplassen(final String hensynPaaArbeidsplassen) {
        this.friskmelding.hensynPaaArbeidsplassen = hensynPaaArbeidsplassen;
        return this;
    }

    public Sykmelding withAntarReturSammeArbeidsgiver(final boolean antarReturSammeArbeidsgiver) {
        this.friskmelding.antarReturSammeArbeidsgiver = antarReturSammeArbeidsgiver;
        return this;
    }

    public Sykmelding withAntattDatoReturSammeArbeidsgiver(final LocalDate antattDatoReturSammeArbeidsgiver) {
        this.friskmelding.antattDatoReturSammeArbeidsgiver = antattDatoReturSammeArbeidsgiver;
        return this;
    }

    public Sykmelding withAntarReturAnnenArbeidsgiver(final boolean antarReturAnnenArbeidsgiver) {
        this.friskmelding.antarReturAnnenArbeidsgiver = antarReturAnnenArbeidsgiver;
        return this;
    }

    public Sykmelding withTilbakemeldingReturArbeid(final LocalDate tilbakemeldingReturArbeid) {
        this.friskmelding.tilbakemeldingReturArbeid = tilbakemeldingReturArbeid;
        return this;
    }

    public Sykmelding withUtenArbeidsgiverAntarTilbakeIArbeid(final boolean utenArbeidsgiverAntarTilbakeIArbeid) {
        this.friskmelding.utenArbeidsgiverAntarTilbakeIArbeid = utenArbeidsgiverAntarTilbakeIArbeid;
        return this;
    }

    public Sykmelding withUtenArbeidsgiverAntarTilbakeIArbeidDato(final LocalDate utenArbeidsgiverAntarTilbakeIArbeidDato) {
        this.friskmelding.utenArbeidsgiverAntarTilbakeIArbeidDato = utenArbeidsgiverAntarTilbakeIArbeidDato;
        return this;
    }

    public Sykmelding withUtenArbeidsgiverTilbakemelding(final LocalDate utenArbeidsgiverTilbakemelding) {
        this.friskmelding.utenArbeidsgiverTilbakemelding = utenArbeidsgiverTilbakemelding;
        return this;
    }

    public Sykmelding withStartLegemeldtFravaer(final LocalDate startLegemeldtFravaer) {
        this.startLegemeldtFravaer = startLegemeldtFravaer;
        return this;
    }

    public Sykmelding withTilretteleggingArbeidsplass(final String tilretteleggingArbeidsplass) {
        this.arbeidsevne.tilretteleggingArbeidsplass = tilretteleggingArbeidsplass;
        return this;
    }

    public Sykmelding withTiltakNAV(final String tiltakNAV) {
        this.arbeidsevne.tiltakNAV = tiltakNAV;
        return this;
    }

    public Sykmelding withTiltakAndre(final String tiltakAndre) {
        this.arbeidsevne.tiltakAndre = tiltakAndre;
        return this;
    }

    public Sykmelding withNavBoerTaTakISaken(final boolean navBoerTaTakISaken) {
        this.meldingTilNav.navBoerTaTakISaken = navBoerTaTakISaken;
        return this;
    }

    public Sykmelding withNavBoerTaTakISakenBegrunnelse(final String navBoerTaTakISakenBegrunnelse) {
        this.meldingTilNav.navBoerTaTakISakenBegrunnelse = navBoerTaTakISakenBegrunnelse;
        return this;
    }

    public Sykmelding withInnspillTilArbeidsgiver(final String innspillTilArbeidsgiver) {
        this.innspillTilArbeidsgiver = innspillTilArbeidsgiver;
        return this;
    }

    public Sykmelding withDokumenterbarPasientkontakt(final LocalDate dokumenterbarPasientkontakt) {
        this.tilbakedatering.dokumenterbarPasientkontakt = dokumenterbarPasientkontakt;
        return this;
    }

    public Sykmelding withTilbakedatertBegrunnelse(final String tilbakedatertBegrunnelse) {
        this.tilbakedatering.tilbakedatertBegrunnelse = tilbakedatertBegrunnelse;
        return this;
    }

    public Sykmelding withSkalViseSkravertFelt(final boolean skalViseSkravertFelt) {
        this.skalViseSkravertFelt = skalViseSkravertFelt;
        return this;
    }

    public Sykmelding withIdentdato(final LocalDate identdato) {
        this.identdato = identdato;
        return this;
    }
}

