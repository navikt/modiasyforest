package no.nav.sbl.dialogarena.modiasyforest.rest.domain.brukerinfo;

public class Bruker {

    public String navn;
    public String arbeidssituasjon;

    public Bruker withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Bruker withArbeidssituasjon(String arbeidssituasjon) {
        this.arbeidssituasjon = arbeidssituasjon;
        return this;
    }
}
