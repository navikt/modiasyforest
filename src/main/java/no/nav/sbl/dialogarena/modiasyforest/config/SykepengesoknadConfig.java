package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.ws.LogErrorHandler;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.STSClientConfig;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.HentSykepengesoeknadListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeRequest;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeResponse;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.modiasyforest.utils.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC;

@Configuration
public class SykepengesoknadConfig {

    public static final String MOCK_KEY = "sykepengesoknad.syfoservice.withmock";

    private SykepengesoeknadV1 port;

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public SykepengesoeknadV1 sykepengesoeknadV1(@Value("${sykepengesoeknad.v1.endpointurl}") String serviceUrl) {
        SykepengesoeknadV1 port = new WsClient<SykepengesoeknadV1>().createPort(serviceUrl, SykepengesoeknadV1.class, singletonList(new LogErrorHandler()));
        STSClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port);
        this.port = port;
        return port;
    }

    public WSHentSykepengesoeknadListeResponse hentSykepengesoeknadListe(WSHentSykepengesoeknadListeRequest request, String OIDCToken) throws HentSykepengesoeknadListeSikkerhetsbegrensning {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken);
        return port.hentSykepengesoeknadListe(request);
    }
}
