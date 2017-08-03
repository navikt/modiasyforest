package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad;

public class Avvik {
    public Double arbeidstimerNormalUke;
    public Integer arbeidsgrad;
    public Double timer;

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
}