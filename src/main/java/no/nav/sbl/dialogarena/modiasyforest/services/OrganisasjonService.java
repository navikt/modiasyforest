package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static java.util.stream.Collectors.joining;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class OrganisasjonService {

    private OrganisasjonV4 organisasjonWebService;

    @Inject
    public OrganisasjonService(OrganisasjonV4 organisasjonWebService) {
        this.organisasjonWebService = organisasjonWebService;
    }

    @Cacheable(cacheNames = "organisasjonnavn", key = "#orgnr", condition = "#orgnr != null")
    public String hentNavn(String orgnr) {
        if (isBlank(orgnr) || !orgnr.matches("\\d{9}$")) {
            log.error("{} prøvde å hente navn med orgnr {}", getIdent().orElse("<Ikke funnet>"), orgnr);
            throw new IllegalArgumentException();
        }
        try {
            WSHentOrganisasjonResponse response = organisasjonWebService.hentOrganisasjon(request(orgnr));
            WSUstrukturertNavn ustrukturertNavn = (WSUstrukturertNavn) response.getOrganisasjon().getNavn();

            return ustrukturertNavn.getNavnelinje().stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(joining(", "));

        } catch (HentOrganisasjonOrganisasjonIkkeFunnet e) {
            log.warn("Kunne ikke hente organisasjon for {}", orgnr);
            return "Fant ikke navn. Orgnummer: " + orgnr;
        } catch (HentOrganisasjonUgyldigInput e) {
            log.warn("Kunne ikke hente organisasjon for {}", orgnr);
            throw new IllegalArgumentException();
        } catch (RuntimeException e) {
            log.error("Feil ved henting av Organisasjon", e);
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
