package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

import java.util.List;

import static java.util.Collections.emptyList;

public class Sporsmal {

    public Ledetekst ledetekst;
    public List<Svar> svar = emptyList();
    public Sporsmalstype type;

    public Sporsmal withLedetekst(final Ledetekst ledetekst) {
        this.ledetekst = ledetekst;
        return this;
    }

    public Sporsmal withSvar(final List<Svar> svar) {
        this.svar = svar;
        return this;
    }

    public Sporsmal withType(final Sporsmalstype type) {
        this.type = type;
        return this;
    }
}
