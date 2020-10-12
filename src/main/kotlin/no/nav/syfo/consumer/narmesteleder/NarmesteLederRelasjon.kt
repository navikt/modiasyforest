package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.controller.narmesteleder.NaermesteLeder
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
fun NarmesteLederRelasjon.toNaermesteLeder(
    navn: String,
    virksomhetsnavn: String
): NaermesteLeder {
    return NaermesteLeder()
        .withId(null)
        .withEpost(this.narmesteLederEpost)
        .withTlf(this.narmesteLederTelefonnummer)
        .withNavn(navn)
        .withOrganisasjonsnavn(virksomhetsnavn)
        .withAktoerId(this.narmesteLederAktorId)
        .withFomDato(this.aktivFom)
        .withOrgnummer(this.orgnummer)
        .withArbeidsgiverForskuttererLoenn(this.arbeidsgiverForskutterer)
        .withAktivTom(null)
        .withErOppgitt(true)
}

