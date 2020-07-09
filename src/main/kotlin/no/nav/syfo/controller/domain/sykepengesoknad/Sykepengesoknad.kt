package no.nav.syfo.controller.domain.sykepengesoknad

import no.nav.syfo.controller.domain.Arbeidsgiver
import no.nav.syfo.controller.domain.sykepengesoknad.oppsumering.Oppsummering
import java.io.Serializable
import java.time.LocalDate

class Sykepengesoknad : Serializable {
    var id: String? = null
    var sykmeldingId: String? = null
    var status: String? = null
    var opprettetDato: LocalDate? = null
    var avbruttDato: LocalDate? = null
    var arbeidsgiver: Arbeidsgiver? = null
    var identdato: LocalDate? = null
    var ansvarBekreftet = false
    var bekreftetKorrektInformasjon = false
    var arbeidsgiverForskutterer: String? = null
    var egenmeldingsperioder: List<Datospenn>? = null
    var gjenopptattArbeidFulltUtDato: LocalDate? = null
    var ferie: List<Datospenn>? = null
    var permisjon: List<Datospenn>? = null
    var utenlandsopphold: Utenlandsopphold? = null
    var aktiviteter: List<Aktivitet>? = null
    var andreInntektskilder: List<AnnenInntektskilde>? = null
    var utdanning: Utdanning? = null
    var sykmeldingSkrevetDato: LocalDate? = null
    var sendtTilArbeidsgiverDato: LocalDate? = null
    var sendtTilNAVDato: LocalDate? = null
    var forrigeSykeforloepTom: LocalDate? = null
    var forrigeSendteSoknadTom: LocalDate? = null
    var korrigerer: String? = null
    var fom: LocalDate? = null
    var tom: LocalDate? = null
    var del = 0
    var oppsummering: Oppsummering? = null
    fun withOppsummering(oppsummering: Oppsummering?): Sykepengesoknad {
        this.oppsummering = oppsummering
        return this
    }

    fun withAnsvarBekreftet(ansvarBekreftet: Boolean): Sykepengesoknad {
        this.ansvarBekreftet = ansvarBekreftet
        return this
    }

    fun withEgenmeldingsperioder(egenmeldingsperioder: List<Datospenn>?): Sykepengesoknad {
        this.egenmeldingsperioder = egenmeldingsperioder
        return this
    }

    fun withGjenopptattArbeidFulltUtDato(gjenopptattArbeidFulltUtDato: LocalDate?): Sykepengesoknad {
        this.gjenopptattArbeidFulltUtDato = gjenopptattArbeidFulltUtDato
        return this
    }

    fun withFerie(ferie: List<Datospenn>?): Sykepengesoknad {
        this.ferie = ferie
        return this
    }

    fun withPermisjon(permisjon: List<Datospenn>?): Sykepengesoknad {
        this.permisjon = permisjon
        return this
    }

    fun withUtenlandsopphold(utenlandsopphold: Utenlandsopphold?): Sykepengesoknad {
        this.utenlandsopphold = utenlandsopphold
        return this
    }

    fun withUtdanning(utdanning: Utdanning?): Sykepengesoknad {
        this.utdanning = utdanning
        return this
    }

    fun withId(id: String?): Sykepengesoknad {
        this.id = id
        return this
    }

    fun withStatus(status: String?): Sykepengesoknad {
        this.status = status
        return this
    }

    fun withOpprettetDato(opprettet: LocalDate?): Sykepengesoknad {
        opprettetDato = opprettet
        return this
    }

    fun withAndreInntektskilder(andreInntektskilder: List<AnnenInntektskilde>?): Sykepengesoknad {
        this.andreInntektskilder = andreInntektskilder
        return this
    }

    fun withBekreftetKorrektInformasjon(bekreftetKorrektInformasjon: Boolean): Sykepengesoknad {
        this.bekreftetKorrektInformasjon = bekreftetKorrektInformasjon
        return this
    }

    fun withIdentdato(identdato: LocalDate?): Sykepengesoknad {
        this.identdato = identdato
        return this
    }

    fun withAvbruttDato(avbruttDato: LocalDate?): Sykepengesoknad {
        this.avbruttDato = avbruttDato
        return this
    }

    fun withAktiviteter(aktiviteter: List<Aktivitet>?): Sykepengesoknad {
        this.aktiviteter = aktiviteter
        return this
    }

    fun withArbeidsgiver(arbeidsgiver: Arbeidsgiver?): Sykepengesoknad {
        this.arbeidsgiver = arbeidsgiver
        return this
    }

    fun withArbeidsgiverForskutterer(arbeidsgiverForskutterer: String?): Sykepengesoknad {
        this.arbeidsgiverForskutterer = arbeidsgiverForskutterer
        return this
    }

    fun withSykmeldingSkrevetDato(sykmeldingSkrevetDato: LocalDate?): Sykepengesoknad {
        this.sykmeldingSkrevetDato = sykmeldingSkrevetDato
        return this
    }

    fun withSendtTilArbeidsgiverDato(sendtTilArbeidsgiverDato: LocalDate?): Sykepengesoknad {
        this.sendtTilArbeidsgiverDato = sendtTilArbeidsgiverDato
        return this
    }

    fun withSendtTilNAVDato(sendtTilNAVDato: LocalDate?): Sykepengesoknad {
        this.sendtTilNAVDato = sendtTilNAVDato
        return this
    }

    fun withForrigeSykeforloepTom(forrigeSykeforloepTom: LocalDate?): Sykepengesoknad {
        this.forrigeSykeforloepTom = forrigeSykeforloepTom
        return this
    }

    fun withKorrigerer(korrigerer: String?): Sykepengesoknad {
        this.korrigerer = korrigerer
        return this
    }

    fun withFom(fom: LocalDate?): Sykepengesoknad {
        this.fom = fom
        return this
    }

    fun withTom(tom: LocalDate?): Sykepengesoknad {
        this.tom = tom
        return this
    }

    fun withDel(del: Int): Sykepengesoknad {
        this.del = del
        return this
    }

    fun withForrigeSendteSoknadTom(forrigeSendteSoknadTom: LocalDate?): Sykepengesoknad {
        this.forrigeSendteSoknadTom = forrigeSendteSoknadTom
        return this
    }

    fun withSykmeldingId(sykmeldingId: String?): Sykepengesoknad {
        this.sykmeldingId = sykmeldingId
        return this
    }
}