package no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver;

import java.util.ArrayList;
import java.util.List;

public class Arbeidsgiver {

    public String orgnummer;
    public String navn;
    public List<Naermesteleder> naermesteledere = new ArrayList<>();

    public Arbeidsgiver withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Arbeidsgiver withOrgnummer(String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public Arbeidsgiver withNaermesteledere(List<Naermesteleder> naermesteledere) {
        this.naermesteledere.addAll(naermesteledere);
        return this;
    }

}
