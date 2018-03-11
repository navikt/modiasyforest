package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.ArbeidsforholdMock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AAregConfig {

    private static final String MOCK_KEY = "arbeidsforhold.aareg.withmock";
    private static final String ENDEPUNKT_URL = getProperty("VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL");
    private static final String ENDEPUNKT_NAVN = "ARBEIDSFORHOLD_V3";
    private static final boolean KRITISK = true;

    @Bean
    public ArbeidsforholdV3 arbeidsforholdV3() {
        ArbeidsforholdV3 prod =  arbeidsforholdPortType().configureStsForOnBehalfOfWithJWT().build();
        ArbeidsforholdV3 mock =  new ArbeidsforholdMock();

        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, ArbeidsforholdV3.class);
    }

    @Bean
    public Pingable arbeidsforholdPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final ArbeidsforholdV3 arbeidsforholdPing = arbeidsforholdPortType()
                .configureStsForSystemUserInFSS()
                .build();
        return () -> {
            try {
                arbeidsforholdPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                // TODO: Dette kan fjernes når Arbeidsforhold implementerer sin Ping uten avhengigheter bakover
                if (e.getMessage().contains("Organisasjon")) {
                    return lyktes(new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, "Organisasjon feiler - ignorerer dette", KRITISK));
                }
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<ArbeidsforholdV3> arbeidsforholdPortType() {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .address(ENDEPUNKT_URL);
    }
}
