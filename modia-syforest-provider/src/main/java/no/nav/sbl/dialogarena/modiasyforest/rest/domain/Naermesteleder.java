package no.nav.sbl.dialogarena.modiasyforest.rest.domain;


public class Naermesteleder {

    public String navn;
    public Integer id;
    public String fodselsdato;
    public String tlf;
    public String epost;
    public Boolean aktiv;
    public Arbeidsgiver arbeidsgiver;

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

    public Naermesteleder withAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    public Naermesteleder withArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
        return this;
    }
    public Naermesteleder withId(Integer id) {
        this.id = id;
        return this;
    }

    public Naermesteleder withFodselsdato(String fodselsdato) {
        this.fodselsdato = fodselsdato;
        return this;
    }

}
