package no.nav.syfo.controller.domain.sykepengesoknad.oppsumering

import java.io.Serializable

class Ledetekst : Serializable {
    var nokkel: String? = null
    var tekst: String? = null
    var verdier = emptyMap<String, String>()
    fun withTekst(tekst: String?): Ledetekst {
        this.tekst = tekst
        return this
    }

    fun withNokkel(nokkel: String?): Ledetekst {
        this.nokkel = nokkel
        return this
    }

    fun withVerdier(verdier: Map<String, String>): Ledetekst {
        this.verdier = verdier
        return this
    }
}
