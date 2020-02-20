package no.nav.syfo.controller.domain.sykepengesoknad;

import java.io.Serializable;
import java.util.List;

public class Utenlandsopphold implements Serializable {
    public List<Datospenn> perioder;
    public Boolean soektOmSykepengerIPerioden;

    public Utenlandsopphold withSoektOmSykepengerIPerioden(final Boolean soektOmSykepengerIPerioden) {
        this.soektOmSykepengerIPerioden = soektOmSykepengerIPerioden;
        return this;
    }

    public Utenlandsopphold withDatospenn(List<Datospenn> datospenn) {
        this.perioder = datospenn;
        return this;
    }
}
