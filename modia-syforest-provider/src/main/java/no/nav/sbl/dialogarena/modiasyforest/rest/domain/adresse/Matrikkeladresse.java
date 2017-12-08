package no.nav.sbl.dialogarena.modiasyforest.rest.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Matrikkeladresse extends StrukturertAdresse {
    public String kommunenummer;
    public String gardsnummer;
    public String bruksnummer;
    public String festenummer;
    public String seksjonsnummer;
    public String undernummer;
    public String eiendomsnavn;
    public String postnummer;
}
