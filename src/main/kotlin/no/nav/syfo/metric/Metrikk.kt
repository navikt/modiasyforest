package no.nav.syfo.metric

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Controller
import javax.inject.Inject

@Controller
class Metrikk @Inject constructor(
    private val registry: MeterRegistry
) {
    fun countEvent(navn: String) {
        registry.counter(
            addPrefix(navn),
            Tags.of("type", "info")
        ).increment()
    }

    fun tellEndepunktKall(navn: String) {
        registry.counter(
            navn,
            Tags.of("type", "info")
        ).increment()
    }

    fun countOutgoingReponse(navn: String, statusCode: Int) {
        registry.counter(
            addPrefix(navn),
            Tags.of(
                "type",
                "info",
                "status",
                statusCode.toString()
            )
        ).increment()
    }

    fun tellHttpKall(kode: Int) {
        registry.counter(
            addPrefix("httpstatus"),
            Tags.of(
                "type",
                "info",
                "kode",
                kode.toString()
            )
        ).increment()
    }

    private fun addPrefix(navn: String): String {
        val metricPrefix = "modiasyforest_"
        return metricPrefix + navn
    }
}
