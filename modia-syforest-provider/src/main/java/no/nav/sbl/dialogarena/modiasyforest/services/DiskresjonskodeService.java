package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.IKKE_FOEDSELSNUMMER;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DiskresjonskodeService {
    private static final Logger LOG = LoggerFactory.getLogger(DiskresjonskodeService.class);

    @Inject
    private DiskresjonskodePortType diskresjonskodePortType;

    @Cacheable(value = "diskresjonskode")
    public String diskresjonskode(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente diskresjonskode med fnr {}", getSubjectHandler().getUid(), fnr);
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
        }

        try {
            return diskresjonskodePortType.hentDiskresjonskode(new WSHentDiskresjonskodeRequest()
                    .withIdent(fnr)
            ).getDiskresjonskode();
        } catch (RuntimeException e) {
            LOG.error("{} fikk en Runtimefeil mot TPS ved bruker {}", getSubjectHandler().getUid(), fnr, e);
            throw e;
        }
    }
}
