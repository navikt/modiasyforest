package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.oppsummering;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class Ledetekst {
    public String nokkel;
    public String tekst;
    public Map<String, String> verdier = emptyMap();

    public Ledetekst withTekst(final String tekst) {
        this.tekst = tekst;
        return this;
    }

    public Ledetekst withNokkel(final String nokkel) {
        this.nokkel = nokkel;
        return this;
    }

    public Ledetekst withVerdier(final Map<String, String> verdier) {
        this.verdier = verdier;
        return this;
    }
}
