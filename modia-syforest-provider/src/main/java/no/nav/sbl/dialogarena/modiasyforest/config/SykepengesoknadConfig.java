package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.SykepengesoeknadV1Mock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class SykepengesoknadConfig {

    private static final String MOCK_KEY = "sykepengesoknad.syfoservice.withmock";
    private static final String ENDEPUNKT_URL = getProperty("arbeidsforhold.endpoint.url");
    private static final String ENDEPUNKT_NAVN = "SYKEPENGESOKNAD_V1";
    private static final boolean KRITISK = false;

    @Bean
    public SykepengesoeknadV1 sykepengesoeknadV1() {
        SykepengesoeknadV1 prod =  sykepengesoeknadPortType().configureStsForOnBehalfOfWithJWT().build();
        SykepengesoeknadV1 mock =  new SykepengesoeknadV1Mock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, SykepengesoeknadV1.class);
    }

    @Bean
    public Pingable sykepengesoeknadPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final SykepengesoeknadV1 sykepengesoeknadPing = sykepengesoeknadPortType()
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
        return () -> {
            try {
                sykepengesoeknadPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<SykepengesoeknadV1> sykepengesoeknadPortType() {
        return new CXFClient<>(SykepengesoeknadV1.class)
                .address(getProperty("sykepengesoeknad.endpoint.url"));
    }

}
