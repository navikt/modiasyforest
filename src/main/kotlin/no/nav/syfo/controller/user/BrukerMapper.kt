package no.nav.syfo.controller.user

import no.nav.syfo.controller.user.domain.Bruker
import no.nav.syfo.controller.user.domain.adresse.*
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*
import org.apache.commons.lang3.text.WordUtils
import java.util.*

object BrukerMapper {
    fun WSBruker.toBruker() =
        Bruker(
            navn = hentNavnTilPerson(this),
            bostedsadresse = kanskjeBostedsadresse(this),
            midlertidigAdresseNorge = kanskjeMidlertidigAdresseNorge(this),
            midlertidigAdresseUtland = kanskjeMidlertidigAdresseUtland(this),
            postAdresse = kanskjePostAdresse(this)
        )

    private fun hentNavnTilPerson(wsPerson: WSPerson): String {
        val mellomnavn = Optional.ofNullable(wsPerson.personnavn.mellomnavn).map { s: String -> "$s " }.orElse("")
        val navnFraTps = wsPerson.personnavn.fornavn + " " + mellomnavn + wsPerson.personnavn.etternavn
        return WordUtils.capitalize(navnFraTps.toLowerCase(), '-', ' ')
    }

    private fun kanskjeBostedsadresse(person: WSPerson): Bostedsadresse? {
        return person.bostedsadresse?.let { bostedsadresse ->
            Bostedsadresse(
                strukturertAdresse = bostedsadresse.strukturertAdresse.mapStrukturertAdresse()
            )
        }
    }

    private fun kanskjeMidlertidigAdresseNorge(person: WSBruker): MidlertidigAdresseNorge? {
        return person.midlertidigPostadresse?.let { postadresse ->
            if (postadresse is WSMidlertidigPostadresseNorge) {
                MidlertidigAdresseNorge(
                    strukturertAdresse = postadresse.strukturertAdresse.mapStrukturertAdresse()
                )
            } else {
                null
            }
        }
    }

    private fun kanskjeMidlertidigAdresseUtland(person: WSBruker): MidlertidigAdresseUtland? {
        return person.midlertidigPostadresse?.let { postadresse ->
            if (postadresse is WSMidlertidigPostadresseUtland) {
                MidlertidigAdresseUtland(
                    ustrukturertAdresse = postadresse.ustrukturertAdresse.tilUstrukturertAdresse()
                )
            } else {
                null
            }
        }
    }

    private fun kanskjePostAdresse(person: WSBruker): PostAdresse? {
        return person.postadresse?.let { postadresse ->
            PostAdresse(
                ustrukturertAdresse = postadresse.ustrukturertAdresse.tilUstrukturertAdresse()
            )
        }
    }

    private fun WSStrukturertAdresse.mapStrukturertAdresse(): StrukturertAdresse {
        when (this) {
            is WSGateadresse -> {
                return StrukturertAdresse(
                    landkode = this.landkode.value,
                    tilleggsadresse = this.tilleggsadresse,
                    gateadresse = this.tilGateAdresse()
                )
            }
            is WSPostboksadresseNorsk -> {
                return StrukturertAdresse(
                    landkode = this.landkode.value,
                    tilleggsadresse = this.tilleggsadresse,
                    postboksadresseNorsk = this.tilPostboksadresseNorsk()
                )
            }
            is WSMatrikkeladresse -> {
                return StrukturertAdresse(
                    landkode = this.landkode.value,
                    tilleggsadresse = this.tilleggsadresse,
                    matrikkeladresse = this.tilMatrikkeladresse()
                )
            }
        }
        return this.tilStrukturertAdresse()
    }

    private fun WSStrukturertAdresse.tilStrukturertAdresse(): StrukturertAdresse =
        StrukturertAdresse(
            landkode = this.landkode.value,
            tilleggsadresse = this.tilleggsadresse
        )

    private fun WSMatrikkeladresse.tilMatrikkeladresse() =
        Matrikkeladresse(
            eiendomsnavn = this.eiendomsnavn,
            gardsnummer = this.matrikkelnummer.gaardsnummer,
            bruksnummer = this.matrikkelnummer.bruksnummer,
            festenummer = this.matrikkelnummer.festenummer,
            seksjonsnummer = this.matrikkelnummer.seksjonsnummer,
            undernummer = this.matrikkelnummer.undernummer,
            postnummer = this.poststed.value
        )

    private fun WSPostboksadresseNorsk.tilPostboksadresseNorsk() =
        PostboksadresseNorsk(
            poststed = this.poststed.value,
            postboksanlegg = this.postboksanlegg,
            postboksnummer = this.postboksnummer
        )

    private fun WSGateadresse.tilGateAdresse() =
        Gateadresse(
            gatenavn = this.gatenavn,
            husnummer = this.husnummer,
            husbokstav = this.husbokstav,
            gatenummer = this.gatenummer,
            kommunenummer = this.kommunenummer,
            postnummer = this.poststed.value,
        )

    private fun WSUstrukturertAdresse.tilUstrukturertAdresse() =
        UstrukturertAdresse(
            adresselinje1 = this.adresselinje1,
            adresselinje2 = this.adresselinje2,
            adresselinje3 = this.adresselinje3,
            adresselinje4 = this.adresselinje4
        )
}
