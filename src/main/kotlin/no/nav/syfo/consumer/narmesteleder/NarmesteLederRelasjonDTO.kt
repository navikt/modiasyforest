package no.nav.syfo.consumer.narmesteleder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NarmesteLederRelasjonDTO(
    val narmesteLederRelasjon: NarmesteLederDTO
)
