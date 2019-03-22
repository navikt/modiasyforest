package no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad;

import java.time.LocalDate;

public class Utdanning {
    public LocalDate utdanningStartdato;
    public boolean erUtdanningFulltidsstudium;

    public Utdanning withUtdanningStartdato(final LocalDate utdanningStartdato) {
        this.utdanningStartdato = utdanningStartdato;
        return this;
    }

    public Utdanning withErUtdanningFulltidsstudium(final boolean erUtdanningFulltidsstudium) {
        this.erUtdanningFulltidsstudium = erUtdanningFulltidsstudium;
        return this;
    }
}
