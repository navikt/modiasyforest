package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.IKKE_FOEDSELSNUMMER;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.ORGANISASJON_UGYLDIG_INPUT;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

public class OrganisasjonService {
    private static final Logger LOG = getLogger(OrganisasjonService.class);

    @Inject
    private OrganisasjonV4 organisasjonWebService;

    @Cacheable(value = "organisasjon")
    public String hentNavn(String orgnr) {
        if (isBlank(orgnr) || !orgnr.matches("\\d{9}$")) {
            LOG.error("{} prøvde å hente navn med orgnr {}", getSubjectHandler().getUid(), orgnr);
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
        }
        String overstyrOrgnummerForSendingTilAltinnTest = getProperty("altinn.test.overstyr.orgnr");
        if (isNotEmpty(overstyrOrgnummerForSendingTilAltinnTest) && orgnr.equals(overstyrOrgnummerForSendingTilAltinnTest)) {
            return "ALTINN-TEST";
        }

        try {
            WSHentOrganisasjonResponse response = organisasjonWebService.hentOrganisasjon(request(orgnr));
            WSUstrukturertNavn ustrukturertNavn = (WSUstrukturertNavn) response.getOrganisasjon().getNavn();

            return ustrukturertNavn.getNavnelinje().stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(joining(", "));

        } catch (HentOrganisasjonOrganisasjonIkkeFunnet e) {
            LOG.warn("Kunne ikke hente organisasjon for {}", orgnr);
            return "Fant ikke navn. Orgnummer: " + orgnr;
        } catch (HentOrganisasjonUgyldigInput e) {
            LOG.warn("Kunne ikke hente organisasjon for {}", orgnr);
            throw new SyfoException(ORGANISASJON_UGYLDIG_INPUT);
        } catch (RuntimeException e) {
            LOG.error("Feil ved henting av Organisasjon", e);
            return "Fant ikke navn";
        }
    }

    private WSHentOrganisasjonRequest request(String orgnr) {
        return new WSHentOrganisasjonRequest()
                .withOrgnummer(orgnr)
                .withInkluderHierarki(false)
                .withInkluderHistorikk(false);
    }
}
