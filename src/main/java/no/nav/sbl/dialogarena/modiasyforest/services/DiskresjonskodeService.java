package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class DiskresjonskodeService {

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
            log.error("{} fikk en Runtimefeil mot TPS ved bruker", e);
            throw e;
        }
    }
}
