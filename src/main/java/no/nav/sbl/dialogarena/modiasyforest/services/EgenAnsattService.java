package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class EgenAnsattService {

    private EgenAnsattV1 egenAnsattV1;

    @Inject
    public EgenAnsattService(EgenAnsattV1 egenAnsattV1) {
        this.egenAnsattV1 = egenAnsattV1;
    }

    @Cacheable(cacheNames = "egenansatt", key = "#fnr", condition = "#fnr != null")
    public boolean erEgenAnsatt(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("{} prøvde å hente egenansattinfo med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }
        return egenAnsattV1.hentErEgenAnsattEllerIFamilieMedEgenAnsatt(new WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest()
                .withIdent(fnr)
        ).isEgenAnsatt();
    }
}
