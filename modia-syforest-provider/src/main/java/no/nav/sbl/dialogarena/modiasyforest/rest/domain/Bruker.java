package no.nav.sbl.dialogarena.modiasyforest.rest.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Bruker {
    public String navn;
    public Kontaktinfo kontaktinfo;
    public String arbeidssituasjon;
}
