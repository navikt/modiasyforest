package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.ws.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.modiasyforest.utils.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC;

@Configuration
public class DkifConfig {

    public static final String MOCK_KEY = "dkif.withmock";

    private DigitalKontaktinformasjonV1 port;

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1(@Value("${virksomhet.digitalkontakinformasjon.v1.endpointurl}") String serviceUrl) {
        DigitalKontaktinformasjonV1 port = factory(serviceUrl);
        STSClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port);
        this.port = port;
        return port;
    }

    @SuppressWarnings("unchecked")
    private DigitalKontaktinformasjonV1 factory(String serviceUrl) {
        return new WsClient<DigitalKontaktinformasjonV1>()
                .createPort(serviceUrl, DigitalKontaktinformasjonV1.class, singletonList(new LogErrorHandler()));
    }

    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest request, String OIDCToken) throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonPersonIkkeFunnet {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken);
        return port.hentDigitalKontaktinformasjon(request);
    }
}
