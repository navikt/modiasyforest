package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.config.DkifConfig;
import no.nav.syfo.controller.domain.Kontaktinfo;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;
import static no.nav.syfo.config.CacheConfig.CACHENAME_DKIFFNR;
import static no.nav.syfo.utils.OIDCUtil.tokenFraOIDC;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class DkifService {

    private static final Logger log = getLogger(DkifService.class);

    private DkifConfig dkifConfig;
    private OIDCRequestContextHolder contextHolder;

    @Inject
    public DkifService(
            final DkifConfig dkifConfig,
            final OIDCRequestContextHolder contextHolder
    ) {
        this.dkifConfig = dkifConfig;
        this.contextHolder = contextHolder;
    }

    @Cacheable(cacheNames = CACHENAME_DKIFFNR, key = "#fnr", condition = "#fnr != null")
    public Kontaktinfo hentKontaktinfoFnr(String fnr, String oidcIssuer) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente kontaktinfo med fnr");
            throw new IllegalArgumentException();
        }

        try {
            String oidcToken = tokenFraOIDC(this.contextHolder, oidcIssuer);
            WSHentDigitalKontaktinformasjonRequest request = new WSHentDigitalKontaktinformasjonRequest().withPersonident(fnr);
            WSKontaktinformasjon response = dkifConfig.hentDigitalKontaktinformasjon(request, oidcToken).getDigitalKontaktinformasjon();

            if ("true".equalsIgnoreCase(response.getReservasjon())) {
                return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(Kontaktinfo.FeilAarsak.RESERVERT);
            }

            if (!harVerfisertSiste18Mnd(response.getEpostadresse(), response.getMobiltelefonnummer())) {
                return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(Kontaktinfo.FeilAarsak.UTGAATT);
            }

            return new Kontaktinfo()
                    .fnr(fnr)
                    .skalHaVarsel(true)
                    .epost(response.getEpostadresse() != null ? response.getEpostadresse().getValue() : "")
                    .tlf(response.getMobiltelefonnummer() != null ? response.getMobiltelefonnummer().getValue() : "");
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(Kontaktinfo.FeilAarsak.KONTAKTINFO_IKKE_FUNNET);
        } catch (HentDigitalKontaktinformasjonSikkerhetsbegrensing e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(Kontaktinfo.FeilAarsak.SIKKERHETSBEGRENSNING);
        } catch (HentDigitalKontaktinformasjonPersonIkkeFunnet e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(Kontaktinfo.FeilAarsak.PERSON_IKKE_FUNNET);
        } catch (RuntimeException e) {
            log.error("Fikk en uventet feil mot DKIF med fnr. Kaster feil videre", e);
            throw e;
        }
    }

    public boolean harVerfisertSiste18Mnd(WSEpostadresse epostadresse, WSMobiltelefonnummer mobiltelefonnummer) {
        return harVerifisertEpostSiste18Mnd(epostadresse) && harVerifisertMobilSiste18Mnd(mobiltelefonnummer);
    }

    private boolean harVerifisertEpostSiste18Mnd(WSEpostadresse epostadresse) {
        return ofNullable(epostadresse)
                .map(WSEpostadresse::getSistVerifisert)
                .filter(sistVerifisertEpost -> sistVerifisertEpost.isAfter(OffsetDateTime.now().minusMonths(18)))
                .isPresent();
    }

    private boolean harVerifisertMobilSiste18Mnd(WSMobiltelefonnummer mobiltelefonnummer) {
        return ofNullable(mobiltelefonnummer)
                .map(WSMobiltelefonnummer::getSistVerifisert)
                .filter(sistVerifisertEpost -> sistVerifisertEpost.isAfter(OffsetDateTime.now().minusMonths(18)))
                .isPresent();
    }
}
