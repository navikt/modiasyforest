package no.nav.syfo.controller.user.domain.adresse

import java.io.Serializable

open class StrukturertAdresse(
    val landkode: String? = null,
    val tilleggsadresse: String? = null,
    var gateadresse: Gateadresse? = null,
    val matrikkeladresse: Matrikkeladresse? = null,
    var postboksadresseNorsk: PostboksadresseNorsk? = null
) : Serializable
