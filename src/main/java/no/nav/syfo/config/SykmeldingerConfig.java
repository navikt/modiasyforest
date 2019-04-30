package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.*;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.syfo.utils.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC;

@Configuration
public class SykmeldingerConfig {

    public static final String MOCK_KEY = "sykmelding.syfoservice.withmock";

    private SykmeldingV1 port;

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public SykmeldingV1 SykmeldingV1(@Value("${sykmelding.v1.url}") String serviceUrl) {
        SykmeldingV1 port = new WsClient<SykmeldingV1>().createPort(serviceUrl, SykmeldingV1.class, singletonList(new LogErrorHandler()));
        STSClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port);
        this.port = port;
        return port;
    }

    public WSHentSykmeldingListeResponse hentSykmeldingListe(WSHentSykmeldingListeRequest request, String OIDCToken) throws HentSykmeldingListeSikkerhetsbegrensning {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken);
        return port.hentSykmeldingListe(request);
    }

    public WSHentOppfoelgingstilfelleListeResponse hentOppfoelgingstilfelleListe(WSHentOppfoelgingstilfelleListeRequest request, String OIDCToken) throws HentOppfoelgingstilfelleListeSikkerhetsbegrensning {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken);
        return port.hentOppfoelgingstilfelleListe(request);
    }
}
