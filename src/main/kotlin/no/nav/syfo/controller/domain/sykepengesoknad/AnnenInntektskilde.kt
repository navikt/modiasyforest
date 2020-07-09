package no.nav.syfo.controller.domain.sykepengesoknad

import java.io.Serializable

class AnnenInntektskilde : Serializable {
    var sykmeldt = false
    var annenInntektskildeType: AnnenInntektskildeType? = null
    fun withSykmeldt(sykmeldt: Boolean): AnnenInntektskilde {
        this.sykmeldt = sykmeldt
        return this
    }

    fun withAnnenInntektskildeType(annenInntektskildeType: AnnenInntektskildeType?): AnnenInntektskilde {
        this.annenInntektskildeType = annenInntektskildeType
        return this
    }
}
