package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.OffsetDateTime;

import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo.FeilAarsak.*;
import static org.slf4j.LoggerFactory.getLogger;

public class DkifService {
    private static final Logger LOG = getLogger(DkifService.class);

    @Inject
    private DigitalKontaktinformasjonV1 dkifV1;

    public Kontaktinfo hentKontaktinfoFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            throw new RuntimeException();
        }

        try {
            WSKontaktinformasjon response = dkifV1.hentDigitalKontaktinformasjon(new WSHentDigitalKontaktinformasjonRequest().withPersonident(fnr)).getDigitalKontaktinformasjon();
            if ("true".equalsIgnoreCase(response.getReservasjon())) {
                return new Kontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(RESERVERT);
            }

            if (harIkkeVerfisertSiste18Mnd(response.getEpostadresse(), response.getMobiltelefonnummer())) {
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
            LOG.error("Det skjedde en uventet feil mot DKIF. Kaster feil videre");
            throw e;
        }
    }

    private boolean harIkkeVerfisertSiste18Mnd(WSEpostadresse epostadresse, WSMobiltelefonnummer mobiltelefonnummer) {
        OffsetDateTime attenMndSiden = OffsetDateTime.now().minusMonths(18);
        return epostadresse != null && epostadresse.getSistVerifisert() != null && epostadresse.getSistVerifisert().isBefore(attenMndSiden) &&
                mobiltelefonnummer != null && mobiltelefonnummer.getSistVerifisert() != null && mobiltelefonnummer.getSistVerifisert().isBefore(attenMndSiden);
    }
}
