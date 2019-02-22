package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SykefravaerOppfoelgingConfig {

    public static final String MOCK_KEY = "oppfoelging.syfoservice.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1(@Value("${sykefravaersoppfoelging.v1.endpointurl}") String serviceUrl) {
        SykefravaersoppfoelgingV1 port = sykmeldingPortType(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<SykefravaersoppfoelgingV1> sykmeldingPortType(String serviceUrl) {
        return new CXFClient<>(SykefravaersoppfoelgingV1.class)
                .address(serviceUrl);
    }
}
