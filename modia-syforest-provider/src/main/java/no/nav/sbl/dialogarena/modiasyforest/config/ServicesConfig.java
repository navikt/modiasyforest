package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.*;
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

    @Bean
    public OrganisasjonService organisasjonService() {
        return new OrganisasjonService();
    }

    @Bean
    public SykeforloepService sykeforloepService() {
        return new SykeforloepService();
    }

    @Bean
    public TidslinjeService tidslinjeService() {
        return new TidslinjeService();
    }

    @Bean
    public TidslinjeHendelserService tidslinjeHendelserService() { return new TidslinjeHendelserService(); }
}
