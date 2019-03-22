package no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad;

import java.util.List;

public class Utenlandsopphold {
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
