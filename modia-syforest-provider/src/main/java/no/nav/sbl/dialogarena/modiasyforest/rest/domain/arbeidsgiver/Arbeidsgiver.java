package no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver;

public class Arbeidsgiver {

    public String orgnummer;
    public String navn;

    public Arbeidsgiver withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Arbeidsgiver withOrgnummer(String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }
}
