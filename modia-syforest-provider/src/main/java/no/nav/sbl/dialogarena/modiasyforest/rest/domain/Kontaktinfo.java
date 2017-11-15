package no.nav.sbl.dialogarena.modiasyforest.rest.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class Kontaktinfo {

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

