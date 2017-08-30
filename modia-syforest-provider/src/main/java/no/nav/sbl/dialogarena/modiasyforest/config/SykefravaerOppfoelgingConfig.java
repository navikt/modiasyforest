package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.OppfoelgingMock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class SykefravaerOppfoelgingConfig {

    private static final String MOCK_KEY = "oppfoelging.syfoservice.withmock";
    private static final String ENDEPUNKT_URL = getProperty("sykefravaersoppfoelging.endpoint.url");
    private static final String ENDEPUNKT_NAVN = "SYKEFRAVAERSOPPFOELGING_V1";
    private static final boolean KRITISK = true;

    @Bean
    public SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1() {
        SykefravaersoppfoelgingV1 prod =  sykmeldingPortType().configureStsForOnBehalfOfWithJWT().build();
        SykefravaersoppfoelgingV1 mock =  new OppfoelgingMock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, SykefravaersoppfoelgingV1.class);
    }

    private CXFClient<SykefravaersoppfoelgingV1> sykmeldingPortType() {
        return new CXFClient<>(SykefravaersoppfoelgingV1.class)
                .address(ENDEPUNKT_URL);
    }

    @Bean
    public Pingable sykmeldingPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final SykefravaersoppfoelgingV1 oppfoelgingPing = sykmeldingPortType()
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
        return () -> {
            try {
                oppfoelgingPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }
}
