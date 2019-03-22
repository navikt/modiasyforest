package no.nav.sbl.dialogarena.modiasyforest.controller.domain.tilgang;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Tilgang {
    //The data is mapped from the Tilgang-object in syfo-tilgangskontroll, so they have to be compatible
    public boolean harTilgang;
    public String begrunnelse;
}
