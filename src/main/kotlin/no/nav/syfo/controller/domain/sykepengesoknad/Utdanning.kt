package no.nav.syfo.controller.domain.sykepengesoknad

import java.io.Serializable
import java.time.LocalDate

class Utdanning : Serializable {
    var utdanningStartdato: LocalDate? = null
    var erUtdanningFulltidsstudium = false
    fun withUtdanningStartdato(utdanningStartdato: LocalDate?): Utdanning {
        this.utdanningStartdato = utdanningStartdato
        return this
    }

    fun withErUtdanningFulltidsstudium(erUtdanningFulltidsstudium: Boolean): Utdanning {
        this.erUtdanningFulltidsstudium = erUtdanningFulltidsstudium
        return this
    }
}
