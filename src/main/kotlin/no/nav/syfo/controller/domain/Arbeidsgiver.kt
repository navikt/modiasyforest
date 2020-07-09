package no.nav.syfo.controller.domain

import java.io.Serializable

class Arbeidsgiver : Serializable {
    var navn: String? = null
    lateinit var orgnummer: String
    fun withNavn(navn: String?): Arbeidsgiver {
        this.navn = navn
        return this
    }

    fun withOrgnummer(orgnummer: String): Arbeidsgiver {
        this.orgnummer = orgnummer
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Arbeidsgiver
        return if (orgnummer == that.orgnummer && navn != null) navn == that.navn else that.navn == null
    }

    override fun hashCode(): Int {
        var result = if (navn != null) navn.hashCode() else 0
        result = 31 * result + orgnummer.hashCode()
        return result
    }
}
