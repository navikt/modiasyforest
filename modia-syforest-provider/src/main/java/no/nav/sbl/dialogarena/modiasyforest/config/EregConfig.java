package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.OrganisasjonMock;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class EregConfig {

    private static final String ORGANISASJON_EREG_MOCK_KEY = "organisasjon.ereg.withmock";

    @Bean
    public OrganisasjonV4 organisasjonV4() {
        OrganisasjonV4 prod = factory().configureStsForOnBehalfOfWithJWT().build();
        OrganisasjonV4 mock = new OrganisasjonMock();

        return createMetricsProxyWithInstanceSwitcher("Organisasjon-EREG", prod, mock, ORGANISASJON_EREG_MOCK_KEY, OrganisasjonV4.class);
    }

//    @Bean
//    public Pingable organisasjonPing() {
//        final OrganisasjonV4 organisasjonPing = factory()
//                .withOutInterceptor(new SystemSAMLOutInterceptor())
//                .build();
//        return () -> {
//            try {
//                organisasjonPing.ping();
//                return lyktes("ORGANISASJON_TJENESTE");
//            } catch (Exception e) {
//                return feilet("ORGANISASJON_TJENESTE", e);
//            }
//        };
//    }

    private CXFClient<OrganisasjonV4> factory() {
        return new CXFClient<>(OrganisasjonV4.class).address(getProperty("organisasjon.endpoint.url"));
    }
}
