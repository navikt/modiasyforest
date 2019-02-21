package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AAregConfig {

    public static final String MOCK_KEY = "arbeidsforhold.aareg.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public ArbeidsforholdV3 arbeidsforholdV3(@Value("${virksomhet.arbeidsforhold.v3.endpointurl}") String serviceUrl) {
        ArbeidsforholdV3 port = arbeidsforholdPortType(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<ArbeidsforholdV3> arbeidsforholdPortType(String serviceUrl) {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .address(serviceUrl);
    }
}
