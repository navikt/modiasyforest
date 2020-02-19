package no.nav.syfo.controller.domain.sykepengesoknad;

import java.io.Serializable;

public class Avvik implements Serializable {
    public Double arbeidstimerNormalUke;
    public Integer arbeidsgrad;
    public Double timer;
    public Integer beregnetArbeidsgrad;

    public Avvik withArbeidstimerNormalUke(final Double arbeidstimerNormalUke) {
        this.arbeidstimerNormalUke = arbeidstimerNormalUke;
        return this;
    }

    public Avvik withArbeidsgrad(final Integer arbeidsgrad) {
        this.arbeidsgrad = arbeidsgrad;
        return this;
    }

    public Avvik withTimer(final Double timer) {
        this.timer = timer;
        return this;
    }

    public Avvik withBeregnetArbeidsgrad(final Integer beregnetArbeidsgrad) {
        this.beregnetArbeidsgrad = beregnetArbeidsgrad;
        return this;
    }
}
