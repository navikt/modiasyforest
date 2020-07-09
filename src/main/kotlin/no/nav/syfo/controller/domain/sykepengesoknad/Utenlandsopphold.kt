package no.nav.syfo.controller.domain.sykepengesoknad

import java.io.Serializable

class Utenlandsopphold : Serializable {
    var perioder: List<Datospenn>? = null
    var soektOmSykepengerIPerioden: Boolean? = null
    fun withSoektOmSykepengerIPerioden(soektOmSykepengerIPerioden: Boolean?): Utenlandsopphold {
        this.soektOmSykepengerIPerioden = soektOmSykepengerIPerioden
        return this
    }

    fun withDatospenn(datospenn: List<Datospenn>?): Utenlandsopphold {
        perioder = datospenn
        return this
    }
}
