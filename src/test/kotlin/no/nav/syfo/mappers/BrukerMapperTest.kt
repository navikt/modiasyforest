package no.nav.syfo.mappers

import no.nav.syfo.controller.domain.mapper.BrukerMapper.toBruker
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigInteger

@RunWith(MockitoJUnitRunner::class)
class BrukerMapperTest {
    @Test
    fun test1() {
        val bruker = WSBruker()
            .withPersonnavn(navnMedMellomnavn)
            .withBostedsadresse(WSBostedsadresse().withStrukturertAdresse(gateadresse))
            .withPostadresse(WSPostadresse().withUstrukturertAdresse(ustrukturertAdresse))
            .withMidlertidigPostadresse(WSMidlertidigPostadresseNorge().withStrukturertAdresse(gateadresse))
            .toBruker()

        Assertions.assertThat(bruker.navn).isEqualTo("Fornavn Mellomnavn Etternavn")

        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.landkode).isEqualTo("landkoder")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.tilleggsadresse).isEqualTo("tilleggsadresse")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.gatenavn).isEqualTo("gatenavn")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.husnummer).isEqualTo(BigInteger("10"))
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.husbokstav).isEqualTo("A")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.gatenummer).isEqualTo(BigInteger("100"))
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.kommunenummer).isEqualTo("kommunenummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.gateadresse?.postnummer).isEqualTo("poststed")

        Assertions.assertThat(bruker.postAdresse?.ustrukturertAdresse?.adresselinje1).isEqualTo("linje1")
        Assertions.assertThat(bruker.postAdresse?.ustrukturertAdresse?.adresselinje2).isEqualTo("linje2")
        Assertions.assertThat(bruker.postAdresse?.ustrukturertAdresse?.adresselinje3).isEqualTo("linje3")
        Assertions.assertThat(bruker.postAdresse?.ustrukturertAdresse?.adresselinje4).isEqualTo("linje4")

        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.landkode).isEqualTo("landkoder")
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.tilleggsadresse).isEqualTo("tilleggsadresse")
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.gatenavn).isEqualTo("gatenavn")
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.husnummer).isEqualTo(BigInteger("10"))
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.husbokstav).isEqualTo("A")
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.gatenummer).isEqualTo(BigInteger("100"))
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.kommunenummer).isEqualTo("kommunenummer")
        Assertions.assertThat(bruker.midlertidigAdresseNorge?.strukturertAdresse?.gateadresse?.postnummer).isEqualTo("poststed")
    }

    @Test
    fun test2() {
        val bruker = WSBruker()
            .withPersonnavn(navn)
            .withBostedsadresse(WSBostedsadresse().withStrukturertAdresse(martikkeladresse))
            .withPostadresse(WSPostadresse().withUstrukturertAdresse(ustrukturertAdresse))
            .withMidlertidigPostadresse(WSMidlertidigPostadresseUtland().withUstrukturertAdresse(ustrukturertAdresse))
            .toBruker()

        Assertions.assertThat(bruker.navn).isEqualTo("Fornavn Etternavn")

        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.landkode).isEqualTo("landkoder")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.tilleggsadresse).isEqualTo("tilleggsadresse")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.eiendomsnavn).isEqualTo("eiendomnavn")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.gardsnummer).isEqualTo("gaardsnummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.bruksnummer).isEqualTo("bruksnummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.festenummer).isEqualTo("festenummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.seksjonsnummer).isEqualTo("seksjonsnummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.undernummer).isEqualTo("undernummer")
        Assertions.assertThat(bruker.bostedsadresse?.strukturertAdresse?.matrikkeladresse?.postnummer).isEqualTo("poststed")

        Assertions.assertThat(bruker.midlertidigAdresseUtland?.ustrukturertAdresse?.adresselinje1).isEqualTo("linje1")
        Assertions.assertThat(bruker.midlertidigAdresseUtland?.ustrukturertAdresse?.adresselinje2).isEqualTo("linje2")
        Assertions.assertThat(bruker.midlertidigAdresseUtland?.ustrukturertAdresse?.adresselinje3).isEqualTo("linje3")
        Assertions.assertThat(bruker.midlertidigAdresseUtland?.ustrukturertAdresse?.adresselinje4).isEqualTo("linje4")
    }
    private val navnMedMellomnavn = WSPersonnavn()
        .withFornavn("fornavn")
        .withMellomnavn("mellomnavn")
        .withEtternavn("etternavn")

    private val navn = WSPersonnavn()
        .withFornavn("fornavn")
        .withEtternavn("etternavn")

    private val gateadresse = WSGateadresse()
        .withLandkode(WSLandkoder().withValue("landkoder"))
        .withTilleggsadresse("tilleggsadresse")
        .withGatenavn("gatenavn")
        .withHusnummer(BigInteger("10"))
        .withHusbokstav("A")
        .withGatenummer(BigInteger("100"))
        .withKommunenummer("kommunenummer")
        .withPoststed(WSPostnummer().withValue("poststed"))

    private val martikkeladresse = WSMatrikkeladresse()
        .withLandkode(WSLandkoder().withValue("landkoder"))
        .withTilleggsadresse("tilleggsadresse")
        .withEiendomsnavn("eiendomnavn")
        .withMatrikkelnummer(WSMatrikkelnummer()
            .withGaardsnummer("gaardsnummer")
            .withBruksnummer("bruksnummer")
            .withFestenummer("festenummer")
            .withSeksjonsnummer("seksjonsnummer")
            .withUndernummer("undernummer")
        )
        .withPoststed(WSPostnummer().withValue("poststed"))

    private val ustrukturertAdresse = WSUstrukturertAdresse()
        .withAdresselinje1("linje1")
        .withAdresselinje2("linje2")
        .withAdresselinje3("linje3")
        .withAdresselinje4("linje4")
}
