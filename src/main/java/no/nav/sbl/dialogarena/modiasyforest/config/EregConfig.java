package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EregConfig {

    public static final String MOCK_KEY = "organisasjon.ereg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public OrganisasjonV4 organisasjonV4(@Value("${virksomhet.organisasjon.v4.endpointurl}") String serviceUrl) {
        OrganisasjonV4 port = factory(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<OrganisasjonV4> factory(String serviceUrl) {
        return new CXFClient<>(OrganisasjonV4.class).address(serviceUrl);
    }
}
