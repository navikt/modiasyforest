package no.nav.sbl.dialogarena.modiasyforest.rest.domain;


import java.time.LocalDate;

public class Naermesteleder {

    public String navn;
    public Integer id;
    public String tlf;
    public String epost;
    public Boolean aktiv;
    public Boolean erOppgitt;
    public LocalDate fomDato;
    public String orgnummer;
    public String organisasjonsNavn;

    public Naermesteleder withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Naermesteleder withTlf(String tlf) {
        this.tlf = tlf;
        return this;
    }

    public Naermesteleder withEpost(String epost) {
        this.epost = epost;
        return this;
    }

    public Naermesteleder withErOppgitt(Boolean erOppgitt) {
        this.erOppgitt = erOppgitt;
        return this;
    }

    public Naermesteleder withAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    public Naermesteleder withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public Naermesteleder withOrganisasjonsnavn(final String organisasjonsNavn) {
        this.organisasjonsNavn = organisasjonsNavn;
        return this;
    }

    public Naermesteleder withId(Integer id) {
        this.id = id;
        return this;
    }

    public Naermesteleder withFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
        return this;
    }

}
