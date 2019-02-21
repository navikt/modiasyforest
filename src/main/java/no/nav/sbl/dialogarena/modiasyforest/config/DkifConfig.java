package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DkifConfig {

    public static final String MOCK_KEY = "dkif.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1(@Value("${virksomhet.digitalkontakinformasjon.v1.endpointurl}") String serviceUrl) {
        DigitalKontaktinformasjonV1 port = factory(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<DigitalKontaktinformasjonV1> factory(String serviceUrl) {
        return new CXFClient<>(DigitalKontaktinformasjonV1.class)
                .address(serviceUrl);
    }
}
