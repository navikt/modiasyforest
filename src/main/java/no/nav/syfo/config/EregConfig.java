package no.nav.syfo.config;

import no.nav.syfo.services.ws.LogErrorHandler;
import no.nav.syfo.services.ws.STSClientConfig;
import no.nav.syfo.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;

@Configuration
public class EregConfig {

    public static final String MOCK_KEY = "organisasjon.ereg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public OrganisasjonV4 organisasjonV4(@Value("${virksomhet.organisasjon.v4.endpointurl}") String serviceUrl) {
        OrganisasjonV4 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private OrganisasjonV4 factory(String serviceUrl) {
        return new WsClient<OrganisasjonV4>()
                .createPort(serviceUrl, OrganisasjonV4.class, singletonList(new LogErrorHandler()));
    }
}
