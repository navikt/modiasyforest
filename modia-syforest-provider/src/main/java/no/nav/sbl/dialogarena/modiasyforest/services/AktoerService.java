package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.AKTOER_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.IKKE_FOEDSELSNUMMER;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class AktoerService {

    private static final Logger LOG = getLogger(AktoerService.class);

    @Inject
    private AktoerV2 aktoerV2;

    @Cacheable(value = "aktoer")
    public String hentAktoerIdForFnr(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente aktoerId med fnr {}", getSubjectHandler().getUid(), fnr);
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
        }

        try {
            return aktoerV2.hentAktoerIdForIdent(
                    new WSHentAktoerIdForIdentRequest()
                            .withIdent(fnr)
            ).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("AktoerID ikke funnet for fødselsnummer {}!", fnr, e);
            throw new SyfoException(AKTOER_IKKE_FUNNET);
        }
    }

    @Cacheable(value = "aktoer")
    public String hentFnrForAktoer(String aktoerId) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            LOG.error("{} prøvde å hente fnr med aktoerId {}", getSubjectHandler().getUid(), aktoerId);
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
        }

        try {
            return aktoerV2.hentIdentForAktoerId(
                    new WSHentIdentForAktoerIdRequest()
                            .withAktoerId(aktoerId)
            ).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.error("Fnr ikke funnet for aktoerId {}!", aktoerId, e);
            throw new SyfoException(AKTOER_IKKE_FUNNET);
        }
    }
}
