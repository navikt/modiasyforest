package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.*;
import no.nav.syfo.services.*;
import no.nav.syfo.services.interfaces.TidslinjeService;
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
        return new TidslinjeServiceImpl();
    }

    @Bean
    public TidslinjeHendelserService tidslinjeHendelserService() { return new TidslinjeHendelserService(); }

    @Bean
    public BrukerprofilService brukerprofilService() {
        return new BrukerprofilService();
    }

    @Bean
    public ArbeidsforholdService arbeidsforholdService() {
        return new ArbeidsforholdService();
    }

    @Bean
    public NaermesteLederService naermesteLederService() {
        return new NaermesteLederService();
    }
}
