package no.nav.syfo.consumer.narmesteleder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.syfo.controller.narmesteleder.NaermesteLeder
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class NarmesteLederDTO(
    val fnr: String,
    val orgnummer: String,
    val narmesteLederFnr: String,
    val narmesteLederTelefonnummer: String,
    val narmesteLederEpost: String,
    val aktivFom: LocalDate,
    val aktivTom: LocalDate? = null,
    val arbeidsgiverForskutterer: Boolean? = null,
    val tilganger: List<Tilgang>,
    val timestamp: OffsetDateTime,
    val navn: String?
) : Serializable

enum class Tilgang {
    SYKMELDING,
    SYKEPENGESOKNAD,
    MOTE,
    OPPFOLGINGSPLAN,
}

fun NarmesteLederDTO.toNaermesteLeder(
    virksomhetsnavn: String
): NaermesteLeder {
    return NaermesteLeder()
        .withId(null)
        .withEpost(this.narmesteLederEpost)
        .withTlf(this.narmesteLederTelefonnummer)
        .withNavn(this.navn)
        .withOrganisasjonsnavn(virksomhetsnavn)
        .withFomDato(this.aktivFom)
        .withOrgnummer(this.orgnummer)
        .withArbeidsgiverForskuttererLoenn(this.arbeidsgiverForskutterer)
        .withAktivTom(this.aktivTom)
        .withErOppgitt(true)
}
