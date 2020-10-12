package no.nav.syfo.controller.user.domain

import no.nav.syfo.controller.user.domain.adresse.*
import java.io.Serializable

data class Bruker(
    val navn: String? = null,
    val kontaktinfo: Kontaktinfo? = null,
    val arbeidssituasjon: String? = null,
    val bostedsadresse: Bostedsadresse? = null,
    val midlertidigAdresseNorge: MidlertidigAdresseNorge? = null,
    val midlertidigAdresseUtland: MidlertidigAdresseUtland? = null,
    val postAdresse: PostAdresse? = null
) : Serializable
