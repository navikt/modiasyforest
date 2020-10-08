package no.nav.syfo.controller.domain.sykmelding

import java.io.Serializable
import java.time.LocalDate

class Datospenn : Serializable {
    var fom: LocalDate? = null
    var tom: LocalDate? = null
    fun withFom(fom: LocalDate?): Datospenn {
        this.fom = fom
        return this
    }

    fun withTom(tom: LocalDate?): Datospenn {
        this.tom = tom
        return this
    }
}
