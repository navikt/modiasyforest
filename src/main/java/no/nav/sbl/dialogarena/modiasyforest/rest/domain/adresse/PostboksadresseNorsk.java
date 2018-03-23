package no.nav.sbl.dialogarena.modiasyforest.rest.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class PostboksadresseNorsk extends StrukturertAdresse {
    public String postnummer;
    public String postboksanlegg;
    private String postboksnummer;
}
