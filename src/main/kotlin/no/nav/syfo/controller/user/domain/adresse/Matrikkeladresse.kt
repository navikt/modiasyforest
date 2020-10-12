package no.nav.syfo.controller.user.domain.adresse

class Matrikkeladresse(
    val kommunenummer: String? = null,
    val gardsnummer: String? = null,
    val bruksnummer: String? = null,
    val festenummer: String? = null,
    val seksjonsnummer: String? = null,
    val undernummer: String? = null,
    val eiendomsnavn: String? = null,
    val postnummer: String? = null
) : StrukturertAdresse()
