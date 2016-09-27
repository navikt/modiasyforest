package no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver;

public class Naermesteleder {

    public String navn;
    public String tlf;
    public String epost;


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
}
