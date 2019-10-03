package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;

@Configuration
public class AAregConfig {

    public static final String MOCK_KEY = "arbeidsforhold.aareg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public ArbeidsforholdV3 arbeidsforholdV3(@Value("${virksomhet.arbeidsforhold.v3.endpointurl}") String serviceUrl) {
        ArbeidsforholdV3 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private ArbeidsforholdV3 factory(String serviceUrl) {
        return new WsClient<ArbeidsforholdV3>()
                .createPort(serviceUrl, ArbeidsforholdV3.class, singletonList(new LogErrorHandler()));
    }
}
