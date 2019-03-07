package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.ws.LogErrorHandler;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.STSClientConfig;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.WsClient;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;

@Configuration
public class EgenAnsattConfig {

    public static final String MOCK_KEY = "organisasjon.ereg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public EgenAnsattV1 egenAnsattV1(@Value("${virksomhet.egenansatt.v1.endpointurl}") String serviceUrl) {
        EgenAnsattV1 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private EgenAnsattV1 factory(String serviceUrl) {
        return new WsClient<EgenAnsattV1>()
                .createPort(serviceUrl, EgenAnsattV1.class, singletonList(new LogErrorHandler()));
    }
}

