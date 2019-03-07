package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.ws.LogErrorHandler;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.STSClientConfig;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
