package no.nav.syfo.controller.domain

import no.nav.syfo.controller.domain.adresse.*
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
