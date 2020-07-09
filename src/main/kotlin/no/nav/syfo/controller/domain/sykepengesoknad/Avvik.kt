package no.nav.syfo.controller.domain.sykepengesoknad

import java.io.Serializable

class Avvik : Serializable {
    var arbeidstimerNormalUke: Double? = null
    var arbeidsgrad: Int? = null
    var timer: Double? = null
    var beregnetArbeidsgrad: Int? = null
    fun withArbeidstimerNormalUke(arbeidstimerNormalUke: Double?): Avvik {
        this.arbeidstimerNormalUke = arbeidstimerNormalUke
        return this
    }

    fun withArbeidsgrad(arbeidsgrad: Int?): Avvik {
        this.arbeidsgrad = arbeidsgrad
        return this
    }

    fun withTimer(timer: Double?): Avvik {
        this.timer = timer
        return this
    }

    fun withBeregnetArbeidsgrad(beregnetArbeidsgrad: Int?): Avvik {
        this.beregnetArbeidsgrad = beregnetArbeidsgrad
        return this
    }
}
