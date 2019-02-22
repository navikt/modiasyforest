package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.ws.LogErrorHandler;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.STSClientConfig;
import no.nav.sbl.dialogarena.modiasyforest.services.ws.WsClient;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeResponse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentOppfoelgingstilfelleListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentSykmeldingListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentSykmeldingListeRequest;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentSykmeldingListeResponse;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.modiasyforest.utils.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC;

@Configuration
public class SykmeldingerConfig {

    public static final String MOCK_KEY = "sykmelding.syfoservice.withmock";

    private SykmeldingV1 port;

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public SykmeldingV1 SykmeldingV1(@Value("${sykmelding.v1.endpointurl}") String serviceUrl) {
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
