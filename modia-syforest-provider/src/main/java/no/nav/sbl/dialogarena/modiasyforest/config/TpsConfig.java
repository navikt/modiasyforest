package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.BrukerprofilMock;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class TpsConfig {


    private static final String BRUKERPROFIL_TPS_MOCK_KEY = "brukerprofilv3.withmock";

    @Bean
    public BrukerprofilV3 organisasjonPortType() {
        BrukerprofilV3 prod = factory().configureStsForOnBehalfOfWithJWT().build();
        BrukerprofilV3 mock = new BrukerprofilMock();

        return createMetricsProxyWithInstanceSwitcher("TPS", prod, mock, BRUKERPROFIL_TPS_MOCK_KEY, BrukerprofilV3.class);
    }

//    @Bean
//    public Pingable organisasjonPing() {
//        final BrukerprofilV3 brukerprofilV3 = factory()
//                .withOutInterceptor(new SystemSAMLOutInterceptor())
//                .build();
//        return () -> {
//            try {
//                brukerprofilV3.ping();
//                return lyktes("BRUKERPROFIL_V3");
//            } catch (Exception e) {
//                return feilet("BRUKERPROFIL_V3", e);
//            }
//        };
//    }

    private CXFClient<BrukerprofilV3> factory() {
        return new CXFClient<>(BrukerprofilV3.class).address(getProperty("brukerprofilv3.endpoint.url"));
    }

}
