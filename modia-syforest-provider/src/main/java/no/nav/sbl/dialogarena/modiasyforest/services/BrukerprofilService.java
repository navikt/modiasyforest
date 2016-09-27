package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class BrukerprofilService {
    private static final Logger LOG = LoggerFactory.getLogger(BrukerprofilService.class);
    @Inject
    private BrukerprofilV3 brukerprofilV3;

    public String hentNavn(String ident) {
        try {
            WSPerson wsPerson = brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(ident))).getBruker();
            return wsPerson.getPersonnavn().getFornavn() + " " + wsPerson.getPersonnavn().getEtternavn();
        } catch (Exception e) {
            LOG.error("Exception mot TPS: {}", e.getMessage());
            return "Vi fant ikke navnet";
        }
    }
}
