package no.nav.syfo.controller.domain.sykepengesoknad;

import java.time.LocalDate;

public class Datospenn {

    public LocalDate fom;
    public LocalDate tom;

    public Datospenn withFom(final LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public Datospenn withTom(final LocalDate tom) {
        this.tom = tom;
        return this;
    }
}
