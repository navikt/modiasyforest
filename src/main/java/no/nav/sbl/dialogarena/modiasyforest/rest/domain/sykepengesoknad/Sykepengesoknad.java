package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.oppsummering.Oppsummering;

import java.time.LocalDate;
import java.util.List;

public class Sykepengesoknad {

    public String id;
    public String sykmeldingId;
    public String status;
    public LocalDate opprettetDato;
    public LocalDate avbruttDato;
    public Arbeidsgiver arbeidsgiver;
    public LocalDate identdato;
    public boolean ansvarBekreftet;
    public boolean bekreftetKorrektInformasjon;
    public String arbeidsgiverForskutterer;
    public List<Datospenn> egenmeldingsperioder;
    public LocalDate gjenopptattArbeidFulltUtDato;
    public List<Datospenn> ferie;
    public List<Datospenn> permisjon;
    public Utenlandsopphold utenlandsopphold;
    public List<Aktivitet> aktiviteter;
    public List<AnnenInntektskilde> andreInntektskilder;
    public Utdanning utdanning;
    public LocalDate sykmeldingSkrevetDato;
    public LocalDate sendtTilArbeidsgiverDato;
    public LocalDate sendtTilNAVDato;
    public LocalDate forrigeSykeforloepTom;
    public LocalDate forrigeSendteSoknadTom;
    public String korrigerer;
    public LocalDate fom;
    public LocalDate tom;
    public int del;
    public Oppsummering oppsummering;

    public Sykepengesoknad withOppsummering(final Oppsummering oppsummering) {
        this.oppsummering = oppsummering;
        return this;
    }

    public Sykepengesoknad withAnsvarBekreftet(final boolean ansvarBekreftet) {
        this.ansvarBekreftet = ansvarBekreftet;
        return this;
    }

    public Sykepengesoknad withEgenmeldingsperioder(final List<Datospenn> egenmeldingsperioder) {
        this.egenmeldingsperioder = egenmeldingsperioder;
        return this;
    }

    public Sykepengesoknad withGjenopptattArbeidFulltUtDato(final LocalDate gjenopptattArbeidFulltUtDato) {
        this.gjenopptattArbeidFulltUtDato = gjenopptattArbeidFulltUtDato;
        return this;
    }

    public Sykepengesoknad withFerie(final List<Datospenn> ferie) {
        this.ferie = ferie;
        return this;
    }

    public Sykepengesoknad withPermisjon(final List<Datospenn> permisjon) {
        this.permisjon = permisjon;
        return this;
    }

    public Sykepengesoknad withUtenlandsopphold(final Utenlandsopphold utenlandsopphold) {
        this.utenlandsopphold = utenlandsopphold;
        return this;
    }

    public Sykepengesoknad withUtdanning(final Utdanning utdanning) {
        this.utdanning = utdanning;
        return this;
    }

    public Sykepengesoknad withId(final String id) {
        this.id = id;
        return this;
    }

    public Sykepengesoknad withStatus(String status) {
        this.status = status;
        return this;
    }

    public Sykepengesoknad withOpprettetDato(LocalDate opprettet) {
        this.opprettetDato = opprettet;
        return this;
    }

    public Sykepengesoknad withAndreInntektskilder(final List<AnnenInntektskilde> andreInntektskilder) {
        this.andreInntektskilder = andreInntektskilder;
        return this;
    }

    public Sykepengesoknad withBekreftetKorrektInformasjon(final boolean bekreftetKorrektInformasjon) {
        this.bekreftetKorrektInformasjon = bekreftetKorrektInformasjon;
        return this;
    }

    public Sykepengesoknad withIdentdato(final LocalDate identdato) {
        this.identdato = identdato;
        return this;
    }

    public Sykepengesoknad withAvbruttDato(LocalDate avbruttDato) {
        this.avbruttDato = avbruttDato;
        return this;
    }

    public Sykepengesoknad withAktiviteter(final List<Aktivitet> aktiviteter) {
        this.aktiviteter = aktiviteter;
        return this;
    }

    public Sykepengesoknad withArbeidsgiver(final Arbeidsgiver arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
        return this;
    }

    public Sykepengesoknad withArbeidsgiverForskutterer(final String arbeidsgiverForskutterer) {
        this.arbeidsgiverForskutterer = arbeidsgiverForskutterer;
        return this;
    }

    public Sykepengesoknad withSykmeldingSkrevetDato(LocalDate sykmeldingSkrevetDato) {
        this.sykmeldingSkrevetDato = sykmeldingSkrevetDato;
        return this;
    }

    public Sykepengesoknad withSendtTilArbeidsgiverDato(final LocalDate sendtTilArbeidsgiverDato) {
        this.sendtTilArbeidsgiverDato = sendtTilArbeidsgiverDato;
        return this;
    }

    public Sykepengesoknad withSendtTilNAVDato(final LocalDate sendtTilNAVDato) {
        this.sendtTilNAVDato = sendtTilNAVDato;
        return this;
    }

    public Sykepengesoknad withForrigeSykeforloepTom(final LocalDate forrigeSykeforloepTom) {
        this.forrigeSykeforloepTom = forrigeSykeforloepTom;
        return this;
    }

    public Sykepengesoknad withKorrigerer(final String korrigerer) {
        this.korrigerer = korrigerer;
        return this;
    }

    public Sykepengesoknad withFom(final LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public Sykepengesoknad withTom(final LocalDate tom) {
        this.tom = tom;
        return this;
    }

    public Sykepengesoknad withDel(final int del) {
        this.del = del;
        return this;
    }

    public Sykepengesoknad withForrigeSendteSoknadTom(final LocalDate forrigeSendteSoknadTom) {
        this.forrigeSendteSoknadTom = forrigeSendteSoknadTom;
        return this;
    }

    public Sykepengesoknad withSykmeldingId(final String sykmeldingId) {
        this.sykmeldingId = sykmeldingId;
        return this;
    }
}