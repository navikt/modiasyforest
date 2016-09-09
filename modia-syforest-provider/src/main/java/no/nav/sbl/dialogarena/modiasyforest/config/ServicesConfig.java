package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    @Bean
    public AktoerService aktoerService() {
        return new AktoerService();
    }

    @Bean
    public SykmeldingService sykmeldingService() {
        return new SykmeldingService();
    }
}
