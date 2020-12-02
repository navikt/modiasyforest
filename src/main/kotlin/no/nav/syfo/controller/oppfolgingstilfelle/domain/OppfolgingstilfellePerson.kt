package no.nav.syfo.controller.oppfolgingstilfelle.domain

import java.time.LocalDate

data class OppfolgingstilfellePerson(
    val fom: LocalDate? = null,
    val tom: LocalDate? = null
)
