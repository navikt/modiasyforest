package no.nav.syfo.services;

import no.nav.syfo.controller.domain.Bruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.xml.ws.soap.SOAPFaultException;

import static no.nav.syfo.mappers.BrukerMapper.ws2bruker;
import static no.nav.syfo.utils.MapUtil.map;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BrukerprofilService {

    private static final Logger log = getLogger(BrukerprofilService.class);

    private final BrukerprofilV3 brukerprofilV3;

    @Inject
    public BrukerprofilService(BrukerprofilV3 brukerprofilV3) {
        this.brukerprofilV3 = brukerprofilV3;
    }

    @Retryable(
            value = {SOAPFaultException.class},
            backoff = @Backoff(delay = 200, maxDelay = 1000)
    )
    @Cacheable(cacheNames = "tpsbruker", key = "#fnr", condition = "#fnr != null")
    public Bruker hentBruker(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente navn med fnr");
            throw new RuntimeException();
        }
        try {
            WSBruker wsBruker = (WSBruker) brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr))).getBruker();
            return map(wsBruker, ws2bruker);
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
            log.error("Fikk RuntimeException mot TPS med ved oppslag", e);
            throw e;
        }
    }

    @Recover
    public void recover(SOAPFaultException e) {
        log.error("Feil ved kall hentKontaktinfo for Ident etter maks antall kall", e);
        throw e;
    }
}
