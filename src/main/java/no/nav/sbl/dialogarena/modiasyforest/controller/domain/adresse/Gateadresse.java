package no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Gateadresse extends StrukturertAdresse {
    public String poststed;
    public String postnummer;
    public BigInteger husnummer;
    public String husbokstav;
    public String kommunenummer;
    public String gatenavn;
    public String bolignummer;
    public BigInteger gatenummer;
}
