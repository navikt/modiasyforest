package no.nav.syfo.narmesteleder

import java.io.Serializable
import java.time.LocalDate

data class NarmesteLederRelasjon(
        val aktorId: String,
        val orgnummer: String,
        val narmesteLederAktorId: String,
        val narmesteLederTelefonnummer: String?,
        val narmesteLederEpost: String?,
        val aktivFom: LocalDate,
        val arbeidsgiverForskutterer: Boolean?,
        val skrivetilgang: Boolean,
        val tilganger: List<Tilgang>
) : Serializable

enum class Tilgang : Serializable {
    SYKMELDING,
    SYKEPENGESOKNAD,
    MOTE,
    OPPFOLGINGSPLAN
}
