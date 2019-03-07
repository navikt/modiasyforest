package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

@Slf4j
@Service
public class BrukerprofilService {

    private BrukerprofilV3 brukerprofilV3;

    @Inject
    public BrukerprofilService(BrukerprofilV3 brukerprofilV3) {
        this.brukerprofilV3 = brukerprofilV3;
    }

    @Cacheable(cacheNames = "tpsnavn", key = "#fnr", condition = "#fnr != null")
    public String hentNavn(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente navn med fnr {}");
            throw new IllegalArgumentException();
        }
        try {
            WSPerson wsPerson = brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr))).getBruker();
            String mellomnavn = wsPerson.getPersonnavn().getMellomnavn() == null ? "" : wsPerson.getPersonnavn().getMellomnavn();
            if (!"".equals(mellomnavn)) {
                mellomnavn = mellomnavn + " ";
            }
            final String navnFraTps = wsPerson.getPersonnavn().getFornavn() + " " + mellomnavn + wsPerson.getPersonnavn().getEtternavn();
            return capitalize(navnFraTps.toLowerCase(), '-', ' ');
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for med FNR", e);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            log.error("Sikkerhetsbegrensning med FNR", e);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet med FNR", e);
            throw new RuntimeException();
        } catch (RuntimeException e) {
            log.error("{} fikk RuntimeException mot TPS med ved oppslag", e);
            return "Vi fant ikke navnet";
        }
    }

    @Cacheable(cacheNames = "tpsbruker", key = "#fnr", condition = "#fnr != null")
    public WSBruker hentBruker(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente navn med fnr");
            throw new RuntimeException();
        }
        try {
            return (WSBruker) brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr))).getBruker();
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt med FNR", e);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            log.error("Sikkerhetsbegrensning med FNR", e);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet med FNR", e);
            throw new RuntimeException();
        } catch (RuntimeException e) {
            log.error("{} fikk RuntimeException mot TPS med ved oppslag", e);
            throw e;
        }
    }
}
