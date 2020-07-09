package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.config.consumer.SykepengesoknadConfig;
import no.nav.syfo.controller.domain.sykepengesoknad.Sykepengesoknad;
import no.nav.syfo.ereg.EregConsumer;
import no.nav.syfo.ereg.Virksomhetsnummer;
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
import static no.nav.syfo.mappers.WS2SykepengesoknadMapper.ws2Sykepengesoknad;
import static no.nav.syfo.oidc.OIDCUtil.tokenFraOIDC;
import static no.nav.syfo.utils.MapUtil.mapListe;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SykepengesoknaderService {

    private static final Logger log = getLogger(SykepengesoknaderService.class);

    @Value("${dev}")
    private String dev;
    private OIDCRequestContextHolder contextHolder;
    private EregConsumer eregConsumer;
    private SykepengesoeknadV1 sykepengesoeknadV1;
    private SykepengesoknadConfig sykepengesoknadConfig;

    @Inject
    public SykepengesoknaderService(
            OIDCRequestContextHolder contextHolder,
            EregConsumer eregConsumer,
            SykepengesoeknadV1 sykepengesoeknadV1,
            SykepengesoknadConfig sykepengesoknadConfig
    ) {
        this.contextHolder = contextHolder;
        this.eregConsumer = eregConsumer;
        this.sykepengesoeknadV1 = sykepengesoeknadV1;
        this.sykepengesoknadConfig = sykepengesoknadConfig;
    }

    @Cacheable(cacheNames = "sykepengesoknad", key = "#aktoerId.concat(#oidcIssuer)", condition = "#aktoerId != null && #oidcIssuer != null")
    public List<Sykepengesoknad> hentSykepengesoknader(String aktoerId, String oidcIssuer) {
        if (isBlank(aktoerId) || !aktoerId.matches("\\d{13}$")) {
            log.error("Pøvde å hente sykepengesoknader med aktoerId {}", aktoerId);
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
            log.error("Har ikke tilgang til søknadene det spørres om: {}", aktoerId, e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("Det skjedde en runtimefeil da {} spurte om søknadene til: {}", aktoerId, e);
            throw e;
        }
    }

    private final Function<Sykepengesoknad, Sykepengesoknad> berikMedArbeidsgiverNavn =
            soknad -> soknad
                    .withArbeidsgiver(soknad.arbeidsgiver
                            .withNavn(eregConsumer
                                    .virksomhetsnavn(new Virksomhetsnummer(soknad.arbeidsgiver.orgnummer))));

}
