package no.nav.syfo.controller.domain;


import java.time.LocalDate;

public class NaermesteLeder {

    public String navn;
    public Long id;
    public String aktoerId;
    public String tlf;
    public String epost;
    public Boolean aktiv;
    public Boolean erOppgitt;
    public LocalDate fomDato;
    public String orgnummer;
    public String organisasjonsnavn;
    public LocalDate aktivTom;
    public Boolean arbeidsgiverForskuttererLoenn;

    public NaermesteLeder withAktoerId(final String aktoerId) {
        this.aktoerId = aktoerId;
        return this;
    }

    public NaermesteLeder withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public NaermesteLeder withTlf(String tlf) {
        this.tlf = tlf;
        return this;
    }

    public NaermesteLeder withEpost(String epost) {
        this.epost = epost;
        return this;
    }

    public NaermesteLeder withErOppgitt(Boolean erOppgitt) {
        this.erOppgitt = erOppgitt;
        return this;
    }

    public NaermesteLeder withAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    public NaermesteLeder withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public NaermesteLeder withOrganisasjonsnavn(final String organisasjonsnavn) {
        this.organisasjonsnavn = organisasjonsnavn;
        return this;
    }

    public NaermesteLeder withId(Long id) {
        this.id = id;
        return this;
    }

    public NaermesteLeder withFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
        return this;
    }

    public Long hentId() {
        return id;
    }

    public NaermesteLeder withAktivTom(final LocalDate aktivTom) {
        this.aktivTom = aktivTom;
        return this;
    }

    public NaermesteLeder withArbeidsgiverForskuttererLoenn(final Boolean arbeidsgiverForskuttererLoenn) {
        this.arbeidsgiverForskuttererLoenn = arbeidsgiverForskuttererLoenn;
        return this;
    }
}
