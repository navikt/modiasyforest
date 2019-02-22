package no.nav.sbl.dialogarena.modiasyforest.mocks;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static no.nav.sbl.dialogarena.modiasyforest.config.TpsConfig.MOCK_KEY;

@Service
@ConditionalOnProperty(value = MOCK_KEY, havingValue = "true")
public class BrukerprofilMock implements BrukerprofilV3 {
    @Override
    public void ping() {
    }

    @Override
    public WSHentKontaktinformasjonOgPreferanserResponse hentKontaktinformasjonOgPreferanser(WSHentKontaktinformasjonOgPreferanserRequest request) throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
        return new WSHentKontaktinformasjonOgPreferanserResponse()
                .withBruker(new WSBruker()
                        .withPersonnavn(new WSPersonnavn()
                                .withFornavn("Test")
                                .withMellomnavn("von")
                                .withEtternavn("Testesen")));
    }
}
