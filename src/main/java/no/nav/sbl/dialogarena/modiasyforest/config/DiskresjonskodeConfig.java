package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DiskresjonskodeConfig {

    public static final String MOCK_KEY = "diskresjonskodev1.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public DiskresjonskodePortType DiskresjonskodePortType(@Value("${virksomhet.diskresjonskode.v1.endpointurl}") String serviceUrl) {
        DiskresjonskodePortType port = factory(serviceUrl)
                .configureStsForSystemUser()
                .build();
        return port;
    }

    private CXFClient<DiskresjonskodePortType> factory(String serviceUrl) {
        return new CXFClient<>(DiskresjonskodePortType.class).address(serviceUrl);
    }
}
