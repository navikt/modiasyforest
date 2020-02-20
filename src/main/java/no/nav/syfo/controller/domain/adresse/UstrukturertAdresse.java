package no.nav.syfo.controller.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class UstrukturertAdresse implements Serializable {
    public String adresselinje1;
    public String adresselinje2;
    public String adresselinje3;
    public String adresselinje4;
    public String landkode;
}
