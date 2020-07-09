package no.nav.syfo.controller.domain.tidslinje

import no.nav.syfo.controller.domain.NaermesteLeder
import java.time.LocalDate
import java.util.*

class Hendelse {
    var id: String? = null
    var inntruffetdato: LocalDate? = null
    var type: Hendelsestype? = null
    var antallDager: Int? = null
    var tekstkey: String? = null
    var data: MutableMap<String, Any?> = HashMap()
    fun withInntruffetdato(inntruffetdato: LocalDate?): Hendelse {
        this.inntruffetdato = inntruffetdato
        return this
    }

    fun withType(type: Hendelsestype?): Hendelse {
        this.type = type
        return this
    }

    fun withData(key: String, data: NaermesteLeder?): Hendelse {
        this.data[key] = data
        return this
    }

    fun withAntallDager(antallDager: Int?): Hendelse {
        this.antallDager = antallDager
        return this
    }

    fun withTekstkey(tekstkey: String?): Hendelse {
        this.tekstkey = tekstkey
        return this
    }

    fun withId(id: String?): Hendelse {
        this.id = id
        return this
    }
}