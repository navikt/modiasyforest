package no.nav.syfo.mocks;

import no.nav.syfo.config.AktoerConfig;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = AktoerConfig.MOCK_KEY, havingValue = "true")
public class AktoerMock implements AktoerV2 {

    private static final String MOCK_AKTORID_PREFIX = "10";
    public static final String AKTOER_ID_MOCK = "1234567893210";

    public WSHentAktoerIdForIdentListeResponse hentAktoerIdForIdentListe(WSHentAktoerIdForIdentListeRequest wsHentAktoerIdForIdentListeRequest) {
        throw new RuntimeException("Ikke implementert i mock. Se AktoerMock");
    }

    public WSHentAktoerIdForIdentResponse hentAktoerIdForIdent(WSHentAktoerIdForIdentRequest wsHentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
        return new WSHentAktoerIdForIdentResponse()
                .withAktoerId(AKTOER_ID_MOCK);
    }

    public WSHentIdentForAktoerIdListeResponse hentIdentForAktoerIdListe(WSHentIdentForAktoerIdListeRequest wsHentIdentForAktoerIdListeRequest) {
        throw new RuntimeException("Ikke implementert i mock. Se AktoerMock");
    }

    public WSHentIdentForAktoerIdResponse hentIdentForAktoerId(WSHentIdentForAktoerIdRequest wsHentIdentForAktoerIdRequest) throws HentIdentForAktoerIdPersonIkkeFunnet {
        throw new RuntimeException("Ikke implementert i mock. Se AktoerMock");
    }

    public void ping() { }

    public static String mockAktorId(String fnr) {
        return MOCK_AKTORID_PREFIX.concat(fnr);
    }
}
