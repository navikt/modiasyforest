package no.nav.syfo.controller.domain.adresse

class PostboksadresseNorsk(
    val poststed: String? = null,
    val postboksanlegg: String? = null,
    private val postboksnummer: String? = null
) : StrukturertAdresse()
