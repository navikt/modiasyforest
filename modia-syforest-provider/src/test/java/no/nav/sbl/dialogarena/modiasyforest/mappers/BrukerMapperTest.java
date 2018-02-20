package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;

import static no.nav.sbl.dialogarena.modiasyforest.mappers.BrukerMapper.ws2bruker;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BrukerMapperTest {

    @Test
    public void test1() {
        Bruker bruker = ws2bruker.apply(new WSBruker()
                .withPersonnavn(navnMedMellomnavn)
                .withBostedsadresse(new WSBostedsadresse().withStrukturertAdresse(gateadresse))
                .withPostadresse(new WSPostadresse().withUstrukturertAdresse(ustrukturertAdresse))
                .withMidlertidigPostadresse(new WSMidlertidigPostadresseNorge().withStrukturertAdresse(gateadresse)));
        assertThat(bruker.navn).isEqualTo("Fornavn Mellomnavn Etternavn");
        assertThat(bruker.bostedsadresse.strukturertAdresse.landkode).isEqualTo("landkoder");
        assertThat(bruker.bostedsadresse.strukturertAdresse.tilleggsadresse).isEqualTo("tilleggsadresse");
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.gatenavn).isEqualTo("gatenavn");
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.husnummer).isEqualTo(new BigInteger("10"));
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.husbokstav).isEqualTo("A");
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.gatenummer).isEqualTo(new BigInteger("100"));
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.kommunenummer).isEqualTo("kommunenummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.gateadresse.postnummer).isEqualTo("poststed");

        assertThat(bruker.postAdresse.ustrukturertAdresse.adresselinje1).isEqualTo("linje1");
        assertThat(bruker.postAdresse.ustrukturertAdresse.adresselinje2).isEqualTo("linje2");
        assertThat(bruker.postAdresse.ustrukturertAdresse.adresselinje3).isEqualTo("linje3");
        assertThat(bruker.postAdresse.ustrukturertAdresse.adresselinje4).isEqualTo("linje4");

        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.landkode).isEqualTo("landkoder");
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.tilleggsadresse).isEqualTo("tilleggsadresse");
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.gatenavn).isEqualTo("gatenavn");
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.husnummer).isEqualTo(new BigInteger("10"));
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.husbokstav).isEqualTo("A");
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.gatenummer).isEqualTo(new BigInteger("100"));
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.kommunenummer).isEqualTo("kommunenummer");
        assertThat(bruker.midlertidigAdresseNorge.strukturertAdresse.gateadresse.postnummer).isEqualTo("poststed");
    }

    @Test
    public void test2() {
        Bruker bruker = ws2bruker.apply(new WSBruker()
                .withPersonnavn(navn)
                .withBostedsadresse(new WSBostedsadresse().withStrukturertAdresse(martikkeladresse))
                .withPostadresse(new WSPostadresse().withUstrukturertAdresse(ustrukturertAdresse))
                .withMidlertidigPostadresse(new WSMidlertidigPostadresseUtland().withUstrukturertAdresse(ustrukturertAdresse)));
        assertThat(bruker.navn).isEqualTo("Fornavn Etternavn");

        assertThat(bruker.bostedsadresse.strukturertAdresse.landkode).isEqualTo("landkoder");
        assertThat(bruker.bostedsadresse.strukturertAdresse.tilleggsadresse).isEqualTo("tilleggsadresse");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.eiendomsnavn).isEqualTo("eiendomnavn");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.gardsnummer).isEqualTo("gaardsnummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.bruksnummer).isEqualTo("bruksnummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.festenummer).isEqualTo("festenummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.seksjonsnummer).isEqualTo("seksjonsnummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.undernummer).isEqualTo("undernummer");
        assertThat(bruker.bostedsadresse.strukturertAdresse.matrikkeladresse.postnummer).isEqualTo("poststed");

        assertThat(bruker.midlertidigAdresseUtland.ustrukturertAdresse.adresselinje1).isEqualTo("linje1");
        assertThat(bruker.midlertidigAdresseUtland.ustrukturertAdresse.adresselinje2).isEqualTo("linje2");
        assertThat(bruker.midlertidigAdresseUtland.ustrukturertAdresse.adresselinje3).isEqualTo("linje3");
        assertThat(bruker.midlertidigAdresseUtland.ustrukturertAdresse.adresselinje4).isEqualTo("linje4");
    }

    private WSPersonnavn navnMedMellomnavn = new WSPersonnavn()
            .withFornavn("fornavn")
            .withMellomnavn("mellomnavn")
            .withEtternavn("etternavn");

    private WSPersonnavn navn = new WSPersonnavn()
            .withFornavn("fornavn")
            .withEtternavn("etternavn");

    private WSGateadresse gateadresse = new WSGateadresse()
            .withLandkode(new WSLandkoder().withValue("landkoder"))
            .withTilleggsadresse("tilleggsadresse")
            .withGatenavn("gatenavn")
            .withHusnummer(new BigInteger("10"))
            .withHusbokstav("A")
            .withGatenummer(new BigInteger("100"))
            .withKommunenummer("kommunenummer")
            .withPoststed(new WSPostnummer().withValue("poststed"));

    private WSMatrikkeladresse martikkeladresse = new WSMatrikkeladresse()
            .withLandkode(new WSLandkoder().withValue("landkoder"))
            .withTilleggsadresse("tilleggsadresse")
            .withEiendomsnavn("eiendomnavn")
            .withMatrikkelnummer(new WSMatrikkelnummer()
                    .withGaardsnummer("gaardsnummer")
                    .withBruksnummer("bruksnummer")
                    .withFestenummer("festenummer")
                    .withSeksjonsnummer("seksjonsnummer")
                    .withUndernummer("undernummer")
            )
            .withPoststed(new WSPostnummer().withValue("poststed"));

    private WSUstrukturertAdresse ustrukturertAdresse = new WSUstrukturertAdresse()
            .withAdresselinje1("linje1")
            .withAdresselinje2("linje2")
            .withAdresselinje3("linje3")
            .withAdresselinje4("linje4");
}
