package no.nav.syfo.controller.domain.tidslinje;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Hendelse {

    public String id;
    public LocalDate inntruffetdato;
    public Hendelsestype type;
    public Integer antallDager;
    public String tekstkey;
    public Map<String, Object> data = new HashMap<>();

    public Hendelse withInntruffetdato(final LocalDate inntruffetdato) {
        this.inntruffetdato = inntruffetdato;
        return this;
    }

    public Hendelse withType(final Hendelsestype type) {
        this.type = type;
        return this;
    }

    public Hendelse withData(String key, Object data) {
        this.data.put(key, data);
        return this;
    }

    public Hendelse withAntallDager(final Integer antallDager) {
        this.antallDager = antallDager;
        return this;
    }

    public Hendelse withTekstkey(final String tekstkey) {
        this.tekstkey = tekstkey;
        return this;
    }

    public Hendelse withId(final String id) {
        this.id = id;
        return this;
    }
}



