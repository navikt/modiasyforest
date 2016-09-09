package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
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
        ServicesConfig.class
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
}
