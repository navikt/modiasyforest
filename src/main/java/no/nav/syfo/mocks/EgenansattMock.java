package no.nav.syfo.mocks;

import no.nav.syfo.config.EgenAnsattConfig;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = EgenAnsattConfig.MOCK_KEY, havingValue = "true")
public class EgenansattMock implements EgenAnsattV1 {
    @Override
    public void ping() {

    }

    @Override
    public WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse hentErEgenAnsattEllerIFamilieMedEgenAnsatt(WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest wsHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest) {
        return new WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse().withEgenAnsatt(false);
    }
}
