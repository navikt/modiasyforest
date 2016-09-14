package no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Tidslinje {

    public LocalDate startdato;
    public List<Hendelse> hendelser = new ArrayList<>();

    public Tidslinje withStartDato(LocalDate startdato) {
        this.startdato = startdato;
        return this;
    }

    public Tidslinje withHendelser(List<Hendelse> hendelser) {
        this.hendelser = hendelser;
        return this;
    }

}
