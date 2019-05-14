package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

import java.util.List;

import static java.util.Collections.emptyList;

public class Svar {
    public Ledetekst ledetekst;
    public Svartype type;
    public Tilleggstekst tilleggstekst;
    public List<Sporsmal> undersporsmal = emptyList();

    public Svar withLedetekst(final Ledetekst ledetekst) {
        this.ledetekst = ledetekst;
        return this;
    }

    public Svar withSvartype(final Svartype type) {
        this.type = type;
        return this;
    }

    public Svar withUndersporsmal(final List<Sporsmal> undersporsmal) {
        this.undersporsmal = undersporsmal;
        return this;
    }

    public Svar withTilleggstekst(final Tilleggstekst tilleggstekst) {
        this.tilleggstekst = tilleggstekst;
        return this;
    }
}
