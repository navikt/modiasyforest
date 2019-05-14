package no.nav.syfo.config;

import no.nav.syfo.services.ws.LogErrorHandler;
import no.nav.syfo.services.ws.STSClientConfig;
import no.nav.syfo.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;

@Configuration
public class TpsConfig {

    public static final String MOCK_KEY = "brukerprofilv3.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public BrukerprofilV3 brukerprofilV3(@Value("${virksomhet.brukerprofil.v3.endpointurl}") String serviceUrl) {
        BrukerprofilV3 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private BrukerprofilV3 factory(String serviceUrl) {
        return new WsClient<BrukerprofilV3>()
                .createPort(serviceUrl, BrukerprofilV3.class, singletonList(new LogErrorHandler()));
    }
}
