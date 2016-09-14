package no.nav.sbl.dialogarena.modiasyforest.rest.domain;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;

public class Arbeidsgiver {

    public String navn;
    public String orgnummer;
    public Boolean erpilotarbeidsgiver;

    public Arbeidsgiver withNavn(final String navn) {
        this.navn = navn;
        return this;
    }

    public Arbeidsgiver withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        this.erpilotarbeidsgiver = asList(getProperty("pilotarbeidsgivere").split(",")).contains(orgnummer);
        return this;
    }
}
