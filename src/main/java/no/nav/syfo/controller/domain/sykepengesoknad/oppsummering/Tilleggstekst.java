package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

import java.io.Serializable;

public class Tilleggstekst implements Serializable {
    public Ledetekst ledetekst;
    public Svartype type;

    public Tilleggstekst withLedetekst(final Ledetekst ledetekst) {
        this.ledetekst = ledetekst;
        return this;
    }

    public Tilleggstekst withType(final Svartype type) {
        this.type = type;
        return this;
    }
}
