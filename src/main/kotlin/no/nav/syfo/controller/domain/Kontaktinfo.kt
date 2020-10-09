package no.nav.syfo.controller.domain

import java.io.Serializable

data class Kontaktinfo(
    val fnr: String? = null,
    val epost: String? = null,
    val tlf: String? = null,
    val skalHaVarsel: Boolean? = null,
    val feilAarsak: FeilAarsak? = null
) : Serializable

enum class FeilAarsak {
    RESERVERT,
    UTGAATT,
    KONTAKTINFO_IKKE_FUNNET,
    SIKKERHETSBEGRENSNING,
    PERSON_IKKE_FUNNET
}
