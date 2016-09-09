package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import org.slf4j.Logger;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.Feil.AKTOER_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.Feil.IKKE_FOEDSELSNUMMER;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class AktoerService {

    private static final Logger LOG = getLogger(AktoerService.class);

    @Inject
    private AktoerV2 aktoerV2;

    public String hentAktoerIdForIdent(String ident) {
        if (isBlank(ident)) {
            LOG.warn("Kan ikke hente aktør-id uten fødselsnummer");
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
        }

        try {
            return aktoerV2.hentAktoerIdForIdent(
                    new WSHentAktoerIdForIdentRequest()
                            .withIdent(ident)
            ).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("AktoerID ikke funnet for fødselsnummer!", e);
            throw new SyfoException(AKTOER_IKKE_FUNNET);
        }
    }
}
