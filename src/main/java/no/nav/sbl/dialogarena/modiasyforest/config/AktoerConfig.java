package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AktoerConfig {

    public static final String MOCK_KEY = "aktoer.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public AktoerV2 aktoerV2(@Value("${aktoer.v2.endpointurl}") String serviceUrl) {
        AktoerV2 port = aktoerPortType(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<AktoerV2> aktoerPortType(String serviceUrl) {
        return new CXFClient<>(AktoerV2.class)
                .address(serviceUrl);
    }
}
