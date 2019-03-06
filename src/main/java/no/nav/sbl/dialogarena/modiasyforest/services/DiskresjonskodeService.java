package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DiskresjonskodeService {
    private static final Logger LOG = LoggerFactory.getLogger(DiskresjonskodeService.class);

    @Inject
    private DiskresjonskodePortType diskresjonskodePortType;

    @Cacheable(cacheNames = "diskresjonskode", key = "#fnr", condition = "#fnr != null")
    public String diskresjonskode(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente diskresjonskode med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }

        try {
            return diskresjonskodePortType.hentDiskresjonskode(new WSHentDiskresjonskodeRequest()
                    .withIdent(fnr)
            ).getDiskresjonskode();
        } catch (RuntimeException e) {
            LOG.error("{} fikk en Runtimefeil mot TPS ved bruker {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
            throw e;
        }
    }
}
