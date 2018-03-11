package no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Tilgang {
    public boolean ikkeTilgang;
    public String ikkeTilgangGrunn;
}
