package no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class UstrukturertAdresse {
    public String adresselinje1;
    public String adresselinje2;
    public String adresselinje3;
    public String adresselinje4;
    public String landkode;
}
