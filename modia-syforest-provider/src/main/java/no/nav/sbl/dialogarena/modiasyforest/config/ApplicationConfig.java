package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
import no.nav.sbl.dialogarena.modiasyforest.selftest.HealthCheckService;
import no.nav.sbl.dialogarena.modiasyforest.selftest.IsAliveServlet;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("no.nav.sbl.dialogarena.modiasyforest")
@Import({
        AktoerConfig.class,
        CacheConfig.class,
        SyfoServiceConfig.class,
        ServicesConfig.class,
        EregConfig.class,
        TpsConfig.class,
        TeksterConfig.class,
        AAregConfig.class,
        SykefravaerOppfoelgingConfig.class
})
public class ApplicationConfig {

    @Bean
    public TimerAspect timerAspect() {
        return new TimerAspect();
    }

    @Bean
    public CountAspect countAspect() {
        return new CountAspect();
    }

    @Bean
    public HealthCheckService healthCheckService() {
        return new HealthCheckService();
    }

    @Bean
    public IsAliveServlet isAliveServlet() {
        return new IsAliveServlet();
    }
}
