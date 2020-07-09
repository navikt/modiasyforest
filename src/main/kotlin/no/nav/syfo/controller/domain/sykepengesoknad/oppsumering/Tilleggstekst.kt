package no.nav.syfo.controller.domain.sykepengesoknad.oppsumering

import java.io.Serializable

class Tilleggstekst : Serializable {
    var ledetekst: Ledetekst? = null
    var type: Svartype? = null
    fun withLedetekst(ledetekst: Ledetekst?): Tilleggstekst {
        this.ledetekst = ledetekst
        return this
    }

    fun withType(type: Svartype?): Tilleggstekst {
        this.type = type
        return this
    }
}
