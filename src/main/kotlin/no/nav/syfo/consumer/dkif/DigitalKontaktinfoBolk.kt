package no.nav.syfo.consumer.dkif

import java.io.Serializable

data class DigitalKontaktinfoBolk (
        val feil: Map<String, Feil>?,
        val kontaktinfo: Map<String, DigitalKontaktinfo>?
)

data class DigitalKontaktinfo(
        val epostadresse: String? = null,
        val kanVarsles: Boolean,
        val reservert: Boolean? = null,
        val mobiltelefonnummer: String? = null,
        val personident: String
) : Serializable

data class Feil(
        val melding: String
)
