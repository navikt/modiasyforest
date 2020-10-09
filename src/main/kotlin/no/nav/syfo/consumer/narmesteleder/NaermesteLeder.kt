package no.nav.syfo.consumer.narmesteleder

import java.io.Serializable
import java.time.LocalDate

class NaermesteLeder : Serializable {
    var navn: String? = null
    var id: Long? = null
    var aktoerId: String? = null
    var tlf: String? = null
    var epost: String? = null
    var aktiv: Boolean? = null
    var erOppgitt: Boolean? = null
    var fomDato: LocalDate? = null
    var orgnummer: String? = null
    var organisasjonsnavn: String? = null
    var aktivTom: LocalDate? = null
    var arbeidsgiverForskuttererLoenn: Boolean? = null
    fun withAktoerId(aktoerId: String?): NaermesteLeder {
        this.aktoerId = aktoerId
        return this
    }

    fun withNavn(navn: String?): NaermesteLeder {
        this.navn = navn
        return this
    }

    fun withTlf(tlf: String?): NaermesteLeder {
        this.tlf = tlf
        return this
    }

    fun withEpost(epost: String?): NaermesteLeder {
        this.epost = epost
        return this
    }

    fun withErOppgitt(erOppgitt: Boolean?): NaermesteLeder {
        this.erOppgitt = erOppgitt
        return this
    }

    fun withAktiv(aktiv: Boolean?): NaermesteLeder {
        this.aktiv = aktiv
        return this
    }

    fun withOrgnummer(orgnummer: String?): NaermesteLeder {
        this.orgnummer = orgnummer
        return this
    }

    fun withOrganisasjonsnavn(organisasjonsnavn: String?): NaermesteLeder {
        this.organisasjonsnavn = organisasjonsnavn
        return this
    }

    fun withId(id: Long?): NaermesteLeder {
        this.id = id
        return this
    }

    fun withFomDato(fomDato: LocalDate?): NaermesteLeder {
        this.fomDato = fomDato
        return this
    }

    fun hentId(): Long? {
        return id
    }

    fun withAktivTom(aktivTom: LocalDate?): NaermesteLeder {
        this.aktivTom = aktivTom
        return this
    }

    fun withArbeidsgiverForskuttererLoenn(arbeidsgiverForskuttererLoenn: Boolean?): NaermesteLeder {
        this.arbeidsgiverForskuttererLoenn = arbeidsgiverForskuttererLoenn
        return this
    }
}
