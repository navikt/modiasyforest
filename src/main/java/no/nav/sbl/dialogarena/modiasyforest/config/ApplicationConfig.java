package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
import no.nav.sbl.dialogarena.modiasyforest.config.caching.CacheConfig;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("no.nav.sbl.dialogarena.modiasyforest")
@Import({
        AktoerConfig.class,
        CacheConfig.class,
        SykmeldingerConfig.class,
        ServicesConfig.class,
        EgenAnsattConfig.class,
        EregConfig.class,
        TpsConfig.class,
        AAregConfig.class,
        SykefravaerOppfoelgingConfig.class,
        SykepengesoknadConfig.class,
        DkifConfig.class,
})
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    @Bean
    public TimerAspect timerAspect() {
        return new TimerAspect();
    }

    @Bean
    public CountAspect countAspect() {
        return new CountAspect();
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .issoLogin()
                .sts();
    }

    @Override
    public String getApplicationName() {
        return "modiasyforest";
    }

    @Override
    public Sone getSone() {
        return Sone.FSS;
    }
}
