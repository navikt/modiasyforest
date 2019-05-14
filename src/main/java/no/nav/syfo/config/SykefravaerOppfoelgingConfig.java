package no.nav.syfo.config;

import no.nav.syfo.services.ws.LogErrorHandler;
import no.nav.syfo.services.ws.STSClientConfig;
import no.nav.syfo.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;

@Configuration
public class SykefravaerOppfoelgingConfig {

    public static final String MOCK_KEY = "oppfoelging.syfoservice.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1(@Value("${sykefravaersoppfoelging.v1.endpointurl}") String serviceUrl) {
        SykefravaersoppfoelgingV1 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private SykefravaersoppfoelgingV1 factory(String serviceUrl) {
        return new WsClient<SykefravaersoppfoelgingV1>()
                .createPort(serviceUrl, SykefravaersoppfoelgingV1.class, singletonList(new LogErrorHandler()));
    }
}
