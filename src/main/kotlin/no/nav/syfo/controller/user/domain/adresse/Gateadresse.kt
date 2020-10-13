package no.nav.syfo.controller.user.domain.adresse

import java.math.BigInteger

data class Gateadresse(
    val poststed: String? = null,
    val postnummer: String? = null,
    val husnummer: BigInteger? = null,
    val husbokstav: String? = null,
    val kommunenummer: String? = null,
    val gatenavn: String? = null,
    val bolignummer: String? = null,
    val gatenummer: BigInteger? = null
) : StrukturertAdresse()
