package no.nav.sbl.dialogarena.modiasyforest.rest.domain;


import java.time.LocalDate;

public class NaermesteLeder {

    public String navn;
    public Long id;
    public String tlf;
    public String epost;
    public Boolean aktiv;
    public Boolean erOppgitt;
    public LocalDate fomDato;
    public String orgnummer;
    public String organisasjonsNavn;

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

    public NaermesteLeder withOrganisasjonsNavn(final String organisasjonsNavn) {
        this.organisasjonsNavn = organisasjonsNavn;
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

}
