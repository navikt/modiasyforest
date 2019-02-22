package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.LocalApplication;
import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelsestype;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSMelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSOppfoelgingstilfelle;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.OidcTestHelper.loggInnVeileder;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.OidcTestHelper.loggUtAlle;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.UserConstants.VEILEDER_ID;
import static no.nav.sbl.dialogarena.modiasyforest.testutils.SykmeldingMocks.getWSSykmelding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class SykeforloepServiceTest {

    @Inject
    private SykeforloepService sykeforloepService;

    @MockBean
    private AktoerService aktoerService;

    @MockBean
    private SykmeldingV1 sykmeldingV1;

    @MockBean
    private NaermesteLederService naermesteLederService;

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
                                                                .withSykmelding(getWSSykmelding())
                                                )))
                        ));
        List<Sykeforloep> sykeforloep = sykeforloepService.hentSykeforloep("12345678901", OIDCIssuer.INTERN);
        assertThat(sykeforloep.get(0).oppfoelgingsdato).isEqualTo(now());
        assertThat(sykeforloep.get(0).hendelser.size()).isEqualTo(1);
        assertThat(sykeforloep.get(0).sykmeldinger.size()).isEqualTo(1);
    }
}
