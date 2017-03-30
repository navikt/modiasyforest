package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.OppfoelgingMock;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class SykefravaerOppfoelgingConfig {

    public static final String SYFOSERVICE_OPPFOELGING_MOCK_KEY = "oppfoelging.syfoservice.withmock";

    @Bean
    public SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1() {
        SykefravaersoppfoelgingV1 prod =  sykmeldingPortType().configureStsForOnBehalfOfWithJWT().build();
        SykefravaersoppfoelgingV1 mock =  new OppfoelgingMock();
        return createMetricsProxyWithInstanceSwitcher("Oppfoelging-SyfoService", prod, mock, SYFOSERVICE_OPPFOELGING_MOCK_KEY, SykefravaersoppfoelgingV1.class);
    }

    private CXFClient<SykefravaersoppfoelgingV1> sykmeldingPortType() {
        return new CXFClient<>(SykefravaersoppfoelgingV1.class)
                .address(getProperty("sykefravaersoppfoelging.endpoint.url"));
    }

//    @Bean
//    public Pingable sykmeldingPing() {
//        final SykefravaersoppfoelgingV1 oppfoelgingPing = sykmeldingPortType()
//                .withOutInterceptor(new SystemSAMLOutInterceptor())
//                .build();
//        return () -> {
//            try {
//                oppfoelgingPing.ping();
//                return lyktes("OPPFOLGING_TJENESTE");
//            } catch (Exception e) {
//                return feilet("OPPFOLGING_TJENESTE", e);
//            }
//        };
//    }
}
