package no.nav.syfo.controller.domain.sykepengesoknad;

import java.io.Serializable;
import java.time.LocalDate;

public class Utdanning implements Serializable {
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
