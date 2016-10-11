package no.nav.sbl.dialogarena.modiasyforest.rest.domain;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;

import java.time.LocalDate;
import java.util.List;


public class Sykeforloep {

    public List<Sykmelding> sykmeldinger;
    public List<Hendelse> hendelser;
    public LocalDate oppfoelgingsdato;

    public Sykeforloep withSykmeldinger(List<Sykmelding> sykmeldinger) {
        this.sykmeldinger = sykmeldinger;
        return this;
    }

    public Sykeforloep withHendelser(List<Hendelse> hendelser) {
        this.hendelser = hendelser;
        return this;
    }

    public Sykeforloep withOppfolgingsdato(LocalDate dato) {
        this.oppfoelgingsdato = dato;
        return this;
    }
}
