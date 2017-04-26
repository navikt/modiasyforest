package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.AktoerMock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AktoerConfig {

    public static final String AKTOER_MOCK_KEY = "aktoer.withmock";

    @Bean
    public AktoerV2 aktoerV2() {
        AktoerV2 prod =  aktoerPortType().configureStsForOnBehalfOfWithJWT().build();
        AktoerV2 mock =  new AktoerMock();

        return createMetricsProxyWithInstanceSwitcher("aktor", prod, mock, AKTOER_MOCK_KEY, AktoerV2.class);
    }

    @Bean
    public Pingable aktoerPing() {
        final AktoerV2 aktoerPing = aktoerPortType()
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
        return () -> {
            try {
                aktoerPing.ping();
                return lyktes("AKTOER_TJENESTE");
            } catch (Exception e) {
                return feilet("AKTOER_TJENESTE", e);
            }
        };
    }

    private CXFClient<AktoerV2> aktoerPortType() {
        return new CXFClient<>(AktoerV2.class)
                .address(getProperty("aktoer.endpoint.url"));
    }
}
