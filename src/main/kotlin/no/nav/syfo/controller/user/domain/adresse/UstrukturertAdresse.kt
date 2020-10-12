package no.nav.syfo.controller.user.domain.adresse

import java.io.Serializable

data class UstrukturertAdresse(
    var adresselinje1: String? = null,
    var adresselinje2: String? = null,
    var adresselinje3: String? = null,
    var adresselinje4: String? = null,
    var landkode: String? = null
) : Serializable
