package no.nav.syfo.utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller
public class Metrikk {

    private final MeterRegistry registry;

    @Inject
    public Metrikk(MeterRegistry registry) {
        this.registry = registry;
    }

    public void tellEndepunktKall(String navn) {
        registry.counter(
                navn,
                Tags.of("type", "info")
        ).increment();
    }

    public void countOutgoingReponse(String navn, Integer statusCode) {
        registry.counter(
                addPrefix(navn),
                Tags.of(
                        "type", "info",
                        "status", statusCode.toString()
                )
        ).increment();
    }

    public void tellHentSykepengesoknader403() {
        String navn = "hentSykepengesoknader.403";
        registry.counter(
                navn,
                Tags.of("type", "info")
        ).increment();
    }

    public void tellHttpKall(int kode) {
        registry.counter(
                addPrefix("httpstatus"),
                Tags.of(
                        "type", "info",
                        "kode", String.valueOf(kode)
                )
        ).increment();
    }

    private String addPrefix(String navn) {
        String METRIKK_PREFIX = "modiasyforest_";
        return METRIKK_PREFIX + navn;
    }
}
