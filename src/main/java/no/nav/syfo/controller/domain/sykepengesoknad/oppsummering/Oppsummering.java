package no.nav.syfo.controller.domain.sykepengesoknad.oppsummering;

import java.io.Serializable;
import java.util.List;

public class Oppsummering implements Serializable {
    public List<Sporsmal> soknad;
    public Sporsmal bekreftetKorrektInformasjon;
    public Tilleggstekst vaerKlarOverAt;

    public Oppsummering withSporsmalsliste(final List<Sporsmal> oppsummering) {
        this.soknad = oppsummering;
        return this;
    }

    public Oppsummering withBekreftetKorrektInformasjon(final Sporsmal bekreftetKorrektInformasjon) {
        this.bekreftetKorrektInformasjon = bekreftetKorrektInformasjon;
        return this;
    }

    public Oppsummering withAnsvarserklaring(final Tilleggstekst ansvarserklaring) {
        this.vaerKlarOverAt = ansvarserklaring;
        return this;
    }
}
