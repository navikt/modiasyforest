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

    @Bean
    public SykepengesoknaderService sykepengesoknaderService() {
        return new SykepengesoknaderService();
    }

    @Bean
    public DkifService dkifService() {
        return new DkifService();
    }

    @Bean
    public DiskresjonskodeService diskresjonskodeService() {
        return new DiskresjonskodeService();
    }

    @Bean
    public EgenAnsattService egenAnsattService() {
        return new EgenAnsattService();
    }

    @Bean
    public TilgangService tilgangService() {
        return new TilgangService();
    }

}
