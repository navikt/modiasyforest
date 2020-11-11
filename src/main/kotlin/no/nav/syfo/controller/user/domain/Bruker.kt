package no.nav.syfo.controller.user.domain

import java.io.Serializable

data class Bruker(
    val navn: String? = null,
    val kontaktinfo: Kontaktinfo? = null,
    val arbeidssituasjon: String? = null
) : Serializable
