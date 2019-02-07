package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.modiasyforest.mappers.BrukerMapper.ws2bruker;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrukerprofilServiceTest {

    @Mock
    private BrukerprofilV3 brukerprofilV3;

    @InjectMocks
    private BrukerprofilService brukerprofilService;


    @Test
    public void captitalizerNavnet() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        when(brukerprofilV3.hentKontaktinformasjonOgPreferanser(any())).thenReturn(new WSHentKontaktinformasjonOgPreferanserResponse().withBruker(new WSBruker().withPersonnavn(new WSPersonnavn().withFornavn("TROND-VIGGO").withEtternavn("TESTESEN"))));
        Bruker bruker = map(brukerprofilService.hentBruker("12345678901"), ws2bruker);
        final String navn = bruker.navn;
        assertThat(navn).isEqualTo("Trond-Viggo Testesen");
    }

    @Test(expected = RuntimeException.class)
    public void taklerIkkeStreng() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        brukerprofilService.hentBruker("mote");
    }

    @Test(expected = RuntimeException.class)
    public void maVaereIdentMedLengde11() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        brukerprofilService.hentBruker("123");
    }

}
