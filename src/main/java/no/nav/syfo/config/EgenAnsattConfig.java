package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;

@Configuration
public class EgenAnsattConfig {

    public static final String MOCK_KEY = "organisasjon.ereg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public EgenAnsattV1 egenAnsattV1(@Value("${egenansatt.v1.url}") String serviceUrl) {
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

