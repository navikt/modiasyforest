package no.nav.syfo.services;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class DiskresjonskodeService {

    private static final Logger log = getLogger(DiskresjonskodeService.class);

    private DiskresjonskodePortType diskresjonskodePortType;

    @Inject
    public DiskresjonskodeService(DiskresjonskodePortType diskresjonskodePortType) {
        this.diskresjonskodePortType = diskresjonskodePortType;
    }

    @Cacheable(cacheNames = "diskresjonskode", key = "#fnr", condition = "#fnr != null")
    public String diskresjonskode(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente diskresjonskode med fnr");
            throw new IllegalArgumentException();
        }

        try {
            return diskresjonskodePortType.hentDiskresjonskode(new WSHentDiskresjonskodeRequest()
                    .withIdent(fnr)
            ).getDiskresjonskode();
        } catch (RuntimeException e) {
            log.error("Fikk en Runtimefeil mot TPS ved bruker", e);
            throw e;
        }
    }
}
