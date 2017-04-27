package services;

import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AktoerServiceTest {

    @Mock
    AktoerV2 aktoerV2;

    @InjectMocks
    private AktoerService aktoerService;

    @Test(expected = SyfoException.class)
    public void exceptionVedTomtFNr() {
        aktoerService.hentAktoerIdForIdent("");
    }

    @Test
    public void hentAktoerId() throws HentAktoerIdForIdentPersonIkkeFunnet {
        WSHentAktoerIdForIdentRequest request = new WSHentAktoerIdForIdentRequest().withIdent("12345678901");
        when(aktoerV2.hentAktoerIdForIdent(request)).thenReturn(new WSHentAktoerIdForIdentResponse().withAktoerId("1234567890112312"));

        String aktoerId = aktoerService.hentAktoerIdForIdent("12345678901");
        assertThat(aktoerId).isEqualTo("1234567890112312");
    }

    @Test(expected = SyfoException.class)
    public void exceptionVedIkkeFunnet() throws HentAktoerIdForIdentPersonIkkeFunnet {
        WSHentAktoerIdForIdentRequest request = new WSHentAktoerIdForIdentRequest().withIdent("999");
        when(aktoerV2.hentAktoerIdForIdent(request)).thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet());

        aktoerService.hentAktoerIdForIdent("999");
    }
}