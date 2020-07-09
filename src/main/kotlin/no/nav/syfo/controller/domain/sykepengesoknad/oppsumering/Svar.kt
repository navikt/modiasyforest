package no.nav.syfo.controller.domain.sykepengesoknad.oppsumering

import java.io.Serializable

class Svar : Serializable {
    var ledetekst: Ledetekst? = null
    var type: Svartype? = null
    var tilleggstekst: Tilleggstekst? = null
    var undersporsmal: List<Sporsmal> = emptyList()
    fun withLedetekst(ledetekst: Ledetekst?): Svar {
        this.ledetekst = ledetekst
        return this
    }

    fun withSvartype(type: Svartype?): Svar {
        this.type = type
        return this
    }

    fun withUndersporsmal(undersporsmal: List<Sporsmal>): Svar {
        this.undersporsmal = undersporsmal
        return this
    }

    fun withTilleggstekst(tilleggstekst: Tilleggstekst?): Svar {
        this.tilleggstekst = tilleggstekst
        return this
    }
}
