package no.nav.syfo.controller.domain.sykepengesoknad.oppsumering

import java.io.Serializable

class Sporsmal : Serializable {
    var ledetekst: Ledetekst? = null
    var svar: List<Svar> = emptyList()
    var type: Sporsmalstype? = null
    fun withLedetekst(ledetekst: Ledetekst?): Sporsmal {
        this.ledetekst = ledetekst
        return this
    }

    fun withSvar(svar: List<Svar>): Sporsmal {
        this.svar = svar
        return this
    }

    fun withType(type: Sporsmalstype?): Sporsmal {
        this.type = type
        return this
    }
}
