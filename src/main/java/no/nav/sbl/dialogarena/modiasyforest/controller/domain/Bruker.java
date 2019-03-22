package no.nav.sbl.dialogarena.modiasyforest.controller.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse.Bostedsadresse;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse.MidlertidigAdresseNorge;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse.MidlertidigAdresseUtland;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse.PostAdresse;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Bruker {
    public String navn;
    public Kontaktinfo kontaktinfo;
    public String arbeidssituasjon;
    public Bostedsadresse bostedsadresse;
    public MidlertidigAdresseNorge midlertidigAdresseNorge;
    public MidlertidigAdresseUtland midlertidigAdresseUtland;
    public PostAdresse postAdresse;
}
