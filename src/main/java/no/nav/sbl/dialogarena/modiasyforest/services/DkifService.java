package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo.FeilAarsak.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class DkifService {

    private DigitalKontaktinformasjonV1 dkifV1;

    @Inject
    public DkifService(DigitalKontaktinformasjonV1 dkifV1) {
        this.dkifV1 = dkifV1;
    }

    @Cacheable(cacheNames = "dkiffnr", key = "#fnr", condition = "#fnr != null")
    public Kontaktinfo hentKontaktinfoFnr(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("{} prøvde å hente kontaktinfo med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }

        try {
            WSKontaktinformasjon response = dkifV1.hentDigitalKontaktinformasjon(new WSHentDigitalKontaktinformasjonRequest().withPersonident(fnr)).getDigitalKontaktinformasjon();
            if ("true".equalsIgnoreCase(response.getReservasjon())) {
                return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(RESERVERT);
            }

            if (!harVerfisertSiste18Mnd(response.getEpostadresse(), response.getMobiltelefonnummer())) {
                return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(UTGAATT);
            }

            return new Kontaktinfo()
                    .fnr(fnr)
                    .skalHaVarsel(true)
                    .epost(response.getEpostadresse() != null ? response.getEpostadresse().getValue() : "")
                    .tlf(response.getMobiltelefonnummer() != null ? response.getMobiltelefonnummer().getValue() : "");
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(KONTAKTINFO_IKKE_FUNNET);
        } catch (HentDigitalKontaktinformasjonSikkerhetsbegrensing e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(SIKKERHETSBEGRENSNING);
        } catch (HentDigitalKontaktinformasjonPersonIkkeFunnet e) {
            return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(PERSON_IKKE_FUNNET);
        } catch (RuntimeException e) {
            log.error("{} fikk en uventet feil mot DKIF med fnr {}. Kaster feil videre", getIdent().orElse("<Ikke funnet>"), fnr, e);
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
