package services;

import no.nav.sbl.dialogarena.modiasyforest.services.BrukerprofilService;
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
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrukerprofilServiceTest {

    @Mock
    BrukerprofilV3 brukerprofilV3;

    @InjectMocks
    BrukerprofilService brukerprofilService;


    @Test
    public void captitalizerNavnet() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        when(brukerprofilV3.hentKontaktinformasjonOgPreferanser(any())).thenReturn(new WSHentKontaktinformasjonOgPreferanserResponse().withBruker(new WSBruker().withPersonnavn(new WSPersonnavn().withFornavn("TROND-VIGGO").withEtternavn("TORGERSEN"))));
        final String navn = brukerprofilService.hentNavn("123");
        assertThat(navn).isEqualTo("Trond-Viggo ***REMOVED***");
    }
}
