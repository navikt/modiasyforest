package no.nav.sbl.dialogarena.modiasyforest.rest.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Bostedsadresse {
    public StrukturertAdresse strukturertAdresse;
}
