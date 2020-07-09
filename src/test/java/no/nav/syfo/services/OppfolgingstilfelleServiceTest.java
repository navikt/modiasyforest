package no.nav.syfo.services;

import no.nav.syfo.LocalApplication;
import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.consumer.NaermesteLederConsumer;
import no.nav.syfo.controller.domain.Sykeforloep;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.testutils.SykmeldingMocks;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class OppfolgingstilfelleServiceTest {

    @Inject
    private OppfolgingstilfelleService oppfolgingstilfelleService;

    @MockBean
    private AktorConsumer aktorConsumer;

    @MockBean
    private SykmeldingV1 sykmeldingV1;

    @MockBean
    private NaermesteLederConsumer naermesteLederConsumer;

    @Test
    public void hentSykeforloep() throws Exception {
        when(sykmeldingV1.hentOppfoelgingstilfelleListe(any())).thenReturn(
                new WSHentOppfoelgingstilfelleListeResponse()
                        .withOppfoelgingstilfelleListe(
                                asList(
                                        new WSOppfoelgingstilfelle()
                                                .withOppfoelgingsdato(now())
                                                .withHendelseListe(
                                                        asList(
                                                                new WSHendelse()
                                                                        .withType(WSHendelsestype.AKTIVITETSKRAV_VARSEL)
                                                                        .withTidspunkt(LocalDateTime.now())))
                                                .withMeldingListe(asList(
                                                        new WSMelding()
                                                                .withSykmelding(SykmeldingMocks.getWSSykmelding())
                                                )))
                        ));
        List<Sykeforloep> sykeforloep = oppfolgingstilfelleService.getOppfolgingstilfelle("12345678901", OIDCIssuer.AZURE);
        assertThat(sykeforloep.get(0).oppfoelgingsdato).isEqualTo(now());
        assertThat(sykeforloep.get(0).hendelser.size()).isEqualTo(1);
        assertThat(sykeforloep.get(0).sykmeldinger.size()).isEqualTo(1);
    }
}
