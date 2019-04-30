package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;

@Configuration
public class DiskresjonskodeConfig {

    public static final String MOCK_KEY = "diskresjonskodev1.withmock";

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public DiskresjonskodePortType DiskresjonskodePortType(@Value("${diskresjonskode.v1.url}") String serviceUrl) {
        DiskresjonskodePortType port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private DiskresjonskodePortType factory(String serviceUrl) {
        return new WsClient<DiskresjonskodePortType>()
                .createPort(serviceUrl, DiskresjonskodePortType.class, singletonList(new LogErrorHandler()));
    }
}
