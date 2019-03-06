package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class AktoerService {

    private AktoerV2 aktoerV2;

    @Inject
    public AktoerService(AktoerV2 aktoerV2) {
        this.aktoerV2 = aktoerV2;
    }

    @Cacheable(cacheNames = "aktoerid", key = "#fnr", condition = "#fnr != null")
    public String hentAktoerIdForFnr(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("{} prøvde å hente aktoerId med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }

        try {
            return aktoerV2.hentAktoerIdForIdent(
                    new WSHentAktoerIdForIdentRequest()
                            .withIdent(fnr)
            ).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            log.warn("AktoerID ikke funnet for fødselsnummer {}!", fnr, e);
            throw new NotFoundException();
        }
    }

    @Cacheable(cacheNames = "aktoerfnr", key = "#aktoerId", condition = "#aktoerId != null")
    public String hentFnrForAktoer(String aktoerId) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            log.error("{} prøvde å hente fnr med aktoerId {}", getIdent().orElse("<Ikke funnet>"), aktoerId);
            throw new IllegalArgumentException();
        }

        try {
            return aktoerV2.hentIdentForAktoerId(
                    new WSHentIdentForAktoerIdRequest()
                            .withAktoerId(aktoerId)
            ).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            log.error("Fnr ikke funnet for aktoerId {}!", aktoerId, e);
            throw new NotFoundException();
        }
    }
}
