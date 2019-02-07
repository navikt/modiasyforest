package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.HentSykepengesoeknadListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeRequest;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeResponse;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.WS2SykepengesoknadMapper.ws2Sykepengesoknad;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.mapListe;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class SykepengesoknaderService {

    private static final Logger LOG = getLogger(SykepengesoknaderService.class);

    @Inject
    private SykepengesoeknadV1 sykepengesoeknadV1;
    @Inject
    private OrganisasjonService organisasjonService;

    @Cacheable(value = "sykepengesoknad", keyGenerator = "userkeygenerator")
    public List<Sykepengesoknad> hentSykepengesoknader(String aktoerId) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            LOG.error("{} prøvde å hente sykepengesoknader med aktoerId {}", getIdent().orElse("<Ikke funnet>"), aktoerId);
            throw new IllegalArgumentException();
        }

        try {
            WSHentSykepengesoeknadListeResponse response = sykepengesoeknadV1.hentSykepengesoeknadListe(new WSHentSykepengesoeknadListeRequest().withAktoerId(aktoerId));
            return mapListe(response.getSykepengesoeknadListe(), ws2Sykepengesoknad)
                    .stream()
                    .map(berikMedArbeidsgiverNavn)
                    .collect(toList());

        } catch (HentSykepengesoeknadListeSikkerhetsbegrensning e) {
            LOG.error("{} har ikke tilgang til søknadene det spørres om: {}", getIdent().orElse("<Ikke funnet>"), aktoerId, e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("Det skjedde en runtimefeil da {} spurte om søknadene til: {}", getIdent().orElse("<Ikke funnet>"), aktoerId, e);
            throw e;
        }
    }

    private final Function<Sykepengesoknad, Sykepengesoknad> berikMedArbeidsgiverNavn =
            soknad -> soknad
                    .withArbeidsgiver(soknad.arbeidsgiver
                            .withNavn(organisasjonService
                                    .hentNavn(soknad.arbeidsgiver.orgnummer)));

}
