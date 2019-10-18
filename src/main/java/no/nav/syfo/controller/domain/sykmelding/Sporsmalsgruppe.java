package no.nav.syfo.controller.domain.sykmelding;

import java.util.ArrayList;
import java.util.List;

public class Sporsmalsgruppe {

    public String id;
    public List<Sporsmal> sporsmal = new ArrayList<>();

    public Sporsmalsgruppe withId(final String id) {
        this.id = id;
        return this;
    }

    public Sporsmalsgruppe withSporsmal(final List<Sporsmal> sporsmal) {
        this.sporsmal = sporsmal;
        return this;
    }
}
