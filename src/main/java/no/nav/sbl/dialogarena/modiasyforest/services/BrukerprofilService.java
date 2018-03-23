package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class BrukerprofilService {
    private static final Logger LOG = LoggerFactory.getLogger(BrukerprofilService.class);
    @Inject
    private BrukerprofilV3 brukerprofilV3;

    @Cacheable(value = "tps", keyGenerator = "userkeygenerator")
    public String hentNavn(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente navn med fnr {}", getSubjectHandler().getUid(), fnr);
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
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for {} med FNR", getSubjectHandler().getUid(), fnr);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            LOG.error("Sikkerhetsbegrensning for {} med FNR {}", getSubjectHandler().getUid(), fnr);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet for {} med FNR", getSubjectHandler().getUid(), fnr);
            throw new RuntimeException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk RuntimeException mot TPS med ved oppslag av {}", getSubjectHandler().getUid(), fnr, e);
            return "Vi fant ikke navnet";
        }
    }

    @Cacheable(value = "tps", keyGenerator = "userkeygenerator")
    public WSBruker hentBruker(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente navn med fnr {}", getSubjectHandler().getUid(), fnr);
            throw new RuntimeException();
        }
        try {
            return (WSBruker) brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr))).getBruker();
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for {} med FNR {}", getSubjectHandler().getUid(), fnr);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            LOG.error("Sikkerhetsbegrensning for {} med FNR {}", getSubjectHandler().getUid(), fnr);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet for {} med FNR {}", getSubjectHandler().getUid(), fnr);
            throw new RuntimeException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk RuntimeException mot TPS med ved oppslag av {}", getSubjectHandler().getUid(), fnr, e);
            throw e;
        }
    }
}
