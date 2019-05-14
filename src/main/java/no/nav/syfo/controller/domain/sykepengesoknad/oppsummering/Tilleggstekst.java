package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

public class Tilleggstekst {
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
