package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

import java.io.Serializable;
import java.util.Map;
import static java.util.Collections.emptyMap;

public class Ledetekst implements Serializable {
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
