package no.nav.syfo.controller.domain.adresse;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class StrukturertAdresse implements Serializable {
    public String landkode;
    public String tilleggsadresse;
    public Gateadresse gateadresse;
    public Matrikkeladresse matrikkeladresse;
    public PostboksadresseNorsk postboksadresseNorsk;
}
