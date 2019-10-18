package no.nav.syfo.controller.domain.sykmelding;

public class Sporsmal {
    public String id;
    public String svar;

    public Sporsmal withId(final String id) {
        this.id = id;
        return this;
    }

    public Sporsmal withSvar(final String svar) {
        this.svar = svar;
        return this;
    }
}
