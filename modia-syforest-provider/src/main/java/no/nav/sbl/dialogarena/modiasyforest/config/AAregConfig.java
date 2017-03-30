package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.ArbeidsforholdMock;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class AAregConfig {

    public static final String ARBEIDSFORHOLD_AAREG_MOCK_KEY = "arbeidsforhold.aareg.withmock";

    @Bean
    public ArbeidsforholdV3 arbeidsforholdV3() {
        ArbeidsforholdV3 prod =  arbeidsforholdPortType().configureStsForOnBehalfOfWithJWT().build();
        ArbeidsforholdV3 mock =  new ArbeidsforholdMock();

        return createMetricsProxyWithInstanceSwitcher("Arbeidsforhold-AAREG", prod, mock, ARBEIDSFORHOLD_AAREG_MOCK_KEY, ArbeidsforholdV3.class);
    }

//    @Bean
//    public Pingable arbeidsforholdPing() {
//        final ArbeidsforholdV3 arbeidsforholdPing = arbeidsforholdPortType()
//                .withOutInterceptor(new SystemSAMLOutInterceptor())
//                .build();
//        return () -> {
//            try {
//                arbeidsforholdPing.ping();
//                return lyktes("ARBEIDSFORHOLD_TJENESTE");
//            } catch (Exception e) {
//                // TODO: Dette kan fjernes når Arbeidsforhold implementerer sin Ping uten avhengigheter bakover
//                if (e.getMessage().contains("Organisasjon")) {
//                    return lyktes("ARBEIDSFORHOLD FEIL: " + e.getMessage());
//                }
//                return feilet("ARBEIDSFORHOLD_TJENESTE", e);
//            }
//        };
//    }

    private CXFClient<ArbeidsforholdV3> arbeidsforholdPortType() {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .address(getProperty("arbeidsforhold.endpoint.url"));
    }
}
