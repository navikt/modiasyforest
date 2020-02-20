package no.nav.syfo.controller.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class Kontaktinfo implements Serializable {

    public String fnr;
    public String epost;
    public String tlf;
    public Boolean skalHaVarsel;
    public FeilAarsak feilAarsak;


    public enum FeilAarsak {
        RESERVERT,
        UTGAATT,
        KONTAKTINFO_IKKE_FUNNET,
        SIKKERHETSBEGRENSNING,
        PERSON_IKKE_FUNNET
    }
}

