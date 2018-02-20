package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelsestype;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSMelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSOppfoelgingstilfelle;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static no.nav.sbl.dialogarena.modiasyforest.testutils.SykmeldingMocks.getWSSykmelding;

@RunWith(MockitoJUnitRunner.class)
public class SykeforloepServiceTest {

    @Mock
    AktoerService aktoerService;

    @Mock
    SykmeldingV1 sykmeldingV1;

    @InjectMocks
    SykeforloepService sykeforloepService;

    @Test
    public void hentSykeforloep() throws Exception {
        when(aktoerService.hentAktoerIdForFnr(null)).thenReturn("456");
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
                                                                        .withDato(now())))
                                                .withMeldingListe(asList(
                                                        new WSMelding()
                                                            .withSykmelding(getWSSykmelding())
                                                )))
                        ));
        List<Sykeforloep> sykeforloep = sykeforloepService.hentSykeforloep("12345678901");
        assertThat(sykeforloep.get(0).oppfoelgingsdato).isEqualTo(now());
        assertThat(sykeforloep.get(0).hendelser.size()).isEqualTo(1);
        assertThat(sykeforloep.get(0).sykmeldinger.size()).isEqualTo(1);
    }
}