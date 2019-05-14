package no.nav.syfo.mocks;

import no.nav.syfo.config.DkifConfig;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@ConditionalOnProperty(value = DkifConfig.MOCK_KEY, havingValue = "true")
public class DKIFMock implements DigitalKontaktinformasjonV1 {
    @Override
    public WSHentSikkerDigitalPostadresseBolkResponse hentSikkerDigitalPostadresseBolk(WSHentSikkerDigitalPostadresseBolkRequest wsHentSikkerDigitalPostadresseBolkRequest) throws HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing, HentSikkerDigitalPostadresseBolkForMangeForespoersler {
        return null;
    }

    @Override
    public WSHentPrintsertifikatResponse hentPrintsertifikat(WSHentPrintsertifikatRequest wsHentPrintsertifikatRequest) {
        return null;
    }

    @Override
    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest wsHentDigitalKontaktinformasjonRequest) throws HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet {
        return new WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(new WSKontaktinformasjon()
                        .withEpostadresse(new WSEpostadresse().withValue("test@nav.no").withSistVerifisert(OffsetDateTime.now().minusDays(10)))
                        .withMobiltelefonnummer(new WSMobiltelefonnummer().withValue("12345678").withSistVerifisert(OffsetDateTime.now().minusDays(10)))
                        .withReservasjon("false"));
    }

    @Override
    public void ping() {

    }

    @Override
    public WSHentDigitalKontaktinformasjonBolkResponse hentDigitalKontaktinformasjonBolk(WSHentDigitalKontaktinformasjonBolkRequest wsHentDigitalKontaktinformasjonBolkRequest) throws HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing, HentDigitalKontaktinformasjonBolkForMangeForespoersler {
        return null;
    }

    @Override
    public WSHentSikkerDigitalPostadresseResponse hentSikkerDigitalPostadresse(WSHentSikkerDigitalPostadresseRequest wsHentSikkerDigitalPostadresseRequest) throws HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet, HentSikkerDigitalPostadresseSikkerhetsbegrensing, HentSikkerDigitalPostadressePersonIkkeFunnet {
        return null;
    }
}
