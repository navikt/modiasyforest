package no.nav.sbl.dialogarena.modiasyforest.utils;

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

    public void tellHentSykepengesoknader403() {
        String navn = "hentSykepengesoknader.403";
        registry.counter(
                navn,
                Tags.of("type", "info")
        ).increment();
    }
}
