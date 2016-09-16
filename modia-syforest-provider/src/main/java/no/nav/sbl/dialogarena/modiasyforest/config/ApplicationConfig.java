package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
import no.nav.sbl.dialogarena.modiasyforest.selftest.HealthCheckService;
import no.nav.sbl.dialogarena.modiasyforest.selftest.IsAliveServlet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.Aktoer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@Import({
        AktoerConfig.class,
        CacheConfig.class,
        SyfoServiceConfig.class,
        ServicesConfig.class,
        EregConfig.class,
        TpsConfig.class
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
