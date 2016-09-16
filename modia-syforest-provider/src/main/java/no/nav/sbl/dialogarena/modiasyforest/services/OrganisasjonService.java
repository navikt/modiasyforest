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

import javax.inject.Inject;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.Feil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

public class OrganisasjonService {
    private static final Logger LOG = getLogger(OrganisasjonService.class);

    @Inject
    private OrganisasjonV4 organisasjonV4;

    public String hentNavn(String orgnr) {
        String overstyrOrgnummerForSendingTilAltinnTest = getProperty("altinn.test.overstyr.orgnr");
        if (isNotEmpty(overstyrOrgnummerForSendingTilAltinnTest) && orgnr.equals(overstyrOrgnummerForSendingTilAltinnTest)) {
            return "ALTINN-TEST";
        }

        try {
            WSHentOrganisasjonResponse response = organisasjonV4.hentOrganisasjon(request(orgnr));
            WSUstrukturertNavn ustrukturertNavn = (WSUstrukturertNavn) response.getOrganisasjon().getNavn();

            return ustrukturertNavn.getNavnelinje().stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(joining(", "));

        } catch (HentOrganisasjonOrganisasjonIkkeFunnet e) {
            LOG.warn("Kunne ikke hente organisasjon for {}", orgnr, e);
            throw new SyfoException(ORGANISASJON_IKKE_FUNNET);
        } catch (HentOrganisasjonUgyldigInput e) {
            LOG.warn("Kunne ikke hente organisasjon for {}", orgnr, e);
            throw new SyfoException(ORGANISASJON_UGYLDIG_INPUT);
        } catch (RuntimeException e) {
            LOG.error("Feil ved henting av Organisasjon", e);
            throw new SyfoException(ORGANISASJON_GENERELL_FEIL);
        }
    }

    private WSHentOrganisasjonRequest request(String orgnr) {
        return new WSHentOrganisasjonRequest()
                .withOrgnummer(orgnr)
                .withInkluderHierarki(false)
                .withInkluderHistorikk(false);
    }
}
