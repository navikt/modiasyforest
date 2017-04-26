package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
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
public class SyfoServiceConfig {

    public static final String SYFOSERVICE_SYKMELDING_MOCK_KEY = "sykmelding.syfoservice.withmock";

    @Bean
    public SykmeldingV1 sykmeldingV1() {
        SykmeldingV1 prod =  sykmeldingPortType().configureStsForOnBehalfOfWithJWT().build();
        SykmeldingV1 mock =  new SykmeldingV1Mock();
        return createMetricsProxyWithInstanceSwitcher("Sykmelding-SyfoService", prod, mock, SYFOSERVICE_SYKMELDING_MOCK_KEY, SykmeldingV1.class);
    }

    private CXFClient<SykmeldingV1> sykmeldingPortType() {
        return new CXFClient<>(SykmeldingV1.class)
                .address(getProperty("sykmelding.endpoint.url"));
    }

    @Bean
    public Pingable sykmeldingPing() {
        final SykmeldingV1 sykmeldingPing = sykmeldingPortType()
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
        return () -> {
            try {
                sykmeldingPing.ping();
                return lyktes("SYKMELDING_TJENESTE");
            } catch (Exception e) {
                return feilet("SYKMELDING_TJENESTE", e);
            }
        };
    }

}
