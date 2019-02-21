package no.nav.sbl.dialogarena.modiasyforest.mocks;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeBolkRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeBolkResponse;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static no.nav.sbl.dialogarena.modiasyforest.config.DiskresjonskodeConfig.MOCK_KEY;

@Service
@ConditionalOnProperty(value = MOCK_KEY, havingValue = "true")
public class DiskresjonskodeMock implements DiskresjonskodePortType {
    @Override
    public WSHentDiskresjonskodeResponse hentDiskresjonskode(WSHentDiskresjonskodeRequest wsHentDiskresjonskodeRequest) {
        return new WSHentDiskresjonskodeResponse().withDiskresjonskode("1");
    }

    @Override
    public WSHentDiskresjonskodeBolkResponse hentDiskresjonskodeBolk(WSHentDiskresjonskodeBolkRequest wsHentDiskresjonskodeBolkRequest) {
        return null;
    }
}
