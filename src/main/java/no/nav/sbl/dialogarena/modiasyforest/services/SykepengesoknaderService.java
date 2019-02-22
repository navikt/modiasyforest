package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.config.SykepengesoknadConfig;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.HentSykepengesoeknadListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeRequest;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.WS2SykepengesoknadMapper.ws2Sykepengesoknad;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.mapListe;
import static no.nav.sbl.dialogarena.modiasyforest.utils.OIDCUtil.tokenFraOIDC;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SykepengesoknaderService {

    private static final Logger LOG = getLogger(SykepengesoknaderService.class);

    @Value("${dev}")
    private String dev;
    private OIDCRequestContextHolder contextHolder;
    private SykepengesoeknadV1 sykepengesoeknadV1;
    private SykepengesoknadConfig sykepengesoknadConfig;
    private OrganisasjonService organisasjonService;

    @Inject
    public SykepengesoknaderService(
            OIDCRequestContextHolder contextHolder,
            OrganisasjonService organisasjonService,
            SykepengesoeknadV1 sykepengesoeknadV1,
            SykepengesoknadConfig sykepengesoknadConfig
    ) {
        this.contextHolder = contextHolder;
        this.organisasjonService = organisasjonService;
        this.sykepengesoeknadV1 = sykepengesoeknadV1;
        this.sykepengesoknadConfig = sykepengesoknadConfig;
    }

    @Cacheable(value = "sykepengesoknad")
    public List<Sykepengesoknad> hentSykepengesoknader(String aktoerId, String oidcIssuer) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            LOG.error("{} prøvde å hente sykepengesoknader med aktoerId {}", getIdent().orElse("<Ikke funnet>"), aktoerId);
            throw new IllegalArgumentException();
        }

        try {
            WSHentSykepengesoeknadListeRequest request = new WSHentSykepengesoeknadListeRequest().withAktoerId(aktoerId);
            WSHentSykepengesoeknadListeResponse response;
            if ("true".equals(dev)) {
                response = sykepengesoeknadV1.hentSykepengesoeknadListe(request);
            } else {
                String oidcToken = tokenFraOIDC(this.contextHolder, oidcIssuer);
                response = sykepengesoknadConfig.hentSykepengesoeknadListe(request, oidcToken);
            }
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
