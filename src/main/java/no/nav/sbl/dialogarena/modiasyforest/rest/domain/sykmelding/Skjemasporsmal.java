package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Datospenn;

import java.util.List;

public class Skjemasporsmal {
    public String arbeidssituasjon;
    public Integer dekningsgrad;
    public Boolean harForsikring;
    public List<Datospenn> fravaersperioder;
    public Boolean harAnnetFravaer;

    public Skjemasporsmal withArbeidssituasjon(final String arbeidssituasjon) {
        this.arbeidssituasjon = arbeidssituasjon;
        return this;
    }

    public Skjemasporsmal withDekningsgrad(final Integer dekningsgrad) {
        this.dekningsgrad = dekningsgrad;
        return this;
    }

    public Skjemasporsmal withHarForsikring(final Boolean harForsikring) {
        this.harForsikring = harForsikring;
        return this;
    }

    public Skjemasporsmal withFravaersperioder(final List<Datospenn> fravaersperioder) {
        this.fravaersperioder = fravaersperioder;
        return this;
    }

    public Skjemasporsmal withHarAnnetFravaer(final Boolean harAnnetFravaer) {
        this.harAnnetFravaer = harAnnetFravaer;
        return this;
    }
}



