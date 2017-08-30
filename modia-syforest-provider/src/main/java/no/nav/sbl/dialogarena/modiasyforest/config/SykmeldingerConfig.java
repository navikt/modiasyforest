package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiasyforest.mocks.SykmeldingV1Mock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class SykmeldingerConfig {

    private static final String MOCK_KEY = "sykmelding.syfoservice.withmock";
    private static final String ENDEPUNKT_URL = getProperty("sykmelding.endpoint.url");
    private static final String ENDEPUNKT_NAVN = "SYKMELDING_V1";
    private static final boolean KRITISK = false;
    @Bean
    public SykmeldingV1 sykmeldingV1() {
        SykmeldingV1 prod =  sykmeldingPortType().configureStsForOnBehalfOfWithJWT().build();
        SykmeldingV1 mock =  new SykmeldingV1Mock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, SykmeldingV1.class);
    }

    private CXFClient<SykmeldingV1> sykmeldingPortType() {
        return new CXFClient<>(SykmeldingV1.class)
                .address(ENDEPUNKT_URL);
    }

    @Bean
    public Pingable sykmeldingPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final SykmeldingV1 sykmeldingPing = sykmeldingPortType()
                .configureStsForSystemUserInFSS()
                .build();
        return () -> {
            try {
                sykmeldingPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

}
