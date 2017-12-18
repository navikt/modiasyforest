package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.WSHentDiskresjonskodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

public class DiskresjonskodeService {
    private static final Logger LOG = LoggerFactory.getLogger(DiskresjonskodeService.class);

    @Inject
    private DiskresjonskodePortType diskresjonskodePortType;

    @Cacheable(value = "diskresjonskode")
    public String diskresjonskode(String fnr) {
        try {
            return diskresjonskodePortType.hentDiskresjonskode(new WSHentDiskresjonskodeRequest()
                    .withIdent(fnr)
            ).getDiskresjonskode();
        } catch (RuntimeException e) {
            LOG.error("Det skjedde en uventet feil mot TPS", e);
            throw new RuntimeException("Feil fra TPS");
        }
    }
}
