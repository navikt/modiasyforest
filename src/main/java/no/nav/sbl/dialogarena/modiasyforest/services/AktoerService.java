package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.virksomhet.aktoer.v2.*;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class AktoerService {

    private static final Logger LOG = getLogger(AktoerService.class);

    @Inject
    private AktoerV2 aktoerV2;

    @Cacheable(value = "aktoer")
    public String hentAktoerIdForFnr(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente aktoerId med fnr {}", getIdent().orElseThrow(IllegalArgumentException::new), fnr);
            throw new IllegalArgumentException();
        }

        try {
            return aktoerV2.hentAktoerIdForIdent(
                    new WSHentAktoerIdForIdentRequest()
                            .withIdent(fnr)
            ).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("AktoerID ikke funnet for fødselsnummer {}!", fnr, e);
            throw new NotFoundException();
        }
    }

    @Cacheable(value = "aktoer")
    public String hentFnrForAktoer(String aktoerId) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            LOG.error("{} prøvde å hente fnr med aktoerId {}", getIdent().orElseThrow(IllegalArgumentException::new), aktoerId);
            throw new IllegalArgumentException();
        }

        try {
            return aktoerV2.hentIdentForAktoerId(
                    new WSHentIdentForAktoerIdRequest()
                            .withAktoerId(aktoerId)
            ).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.error("Fnr ikke funnet for aktoerId {}!", aktoerId, e);
            throw new NotFoundException();
        }
    }
}
