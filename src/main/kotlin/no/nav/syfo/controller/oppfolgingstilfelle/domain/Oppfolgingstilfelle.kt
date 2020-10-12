package no.nav.syfo.controller.oppfolgingstilfelle.domain

import java.time.LocalDate

data class Oppfolgingstilfelle(
    val orgnummer: String,
    val fom: LocalDate? = null,
    val tom: LocalDate? = null,
    val grad: Int = 0,
    val aktivitet: String? = null
)
