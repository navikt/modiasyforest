package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static java.util.stream.Collectors.joining;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class OrganisasjonService {
    private static final Logger LOG = getLogger(OrganisasjonService.class);

    private OrganisasjonV4 organisasjonWebService;

    @Inject
    public OrganisasjonService(OrganisasjonV4 organisasjonWebService) {
        this.organisasjonWebService = organisasjonWebService;
    }

    @Cacheable(value = "organisasjon")
    public String hentNavn(String orgnr) {
        if (isBlank(orgnr) || !orgnr.matches("\\d{9}$")) {
            LOG.error("{} prøvde å hente navn med orgnr {}", getIdent().orElse("<Ikke funnet>"), orgnr);
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
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
