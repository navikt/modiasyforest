package no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad.oppsummering;

import java.util.List;

public class Oppsummering {
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
