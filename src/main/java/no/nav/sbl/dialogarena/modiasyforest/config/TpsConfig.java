package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TpsConfig {

    public static final String MOCK_KEY = "brukerprofilv3.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public BrukerprofilV3 brukerprofilV3(@Value("${virksomhet.brukerprofil.v3.endpointurl}") String serviceUrl) {
        BrukerprofilV3 prod = factory(serviceUrl)
                .configureStsForSystemUser()
                .build();

        return prod;
    }

    private CXFClient<BrukerprofilV3> factory(String serviceUrl) {
        return new CXFClient<>(BrukerprofilV3.class).address(serviceUrl);
    }

}
