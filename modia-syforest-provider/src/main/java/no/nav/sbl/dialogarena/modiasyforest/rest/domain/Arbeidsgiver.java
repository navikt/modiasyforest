package no.nav.sbl.dialogarena.modiasyforest.rest.domain;

public class Arbeidsgiver {

    public String navn;
    public String orgnummer;

    public Arbeidsgiver withNavn(final String navn) {
        this.navn = navn;
        return this;
    }

    public Arbeidsgiver withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arbeidsgiver that = (Arbeidsgiver) o;

        return orgnummer.equals(that.orgnummer) && navn != null ? navn.equals(that.navn) : that.navn == null;
    }

    @Override
    public int hashCode() {
        int result = navn != null ? navn.hashCode() : 0;
        result = 31 * result + orgnummer.hashCode();
        return result;
    }
}
