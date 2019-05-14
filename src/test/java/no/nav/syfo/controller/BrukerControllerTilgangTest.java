package no.nav.syfo.controller;

import no.nav.syfo.LocalApplication;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.controller.domain.Kontaktinfo;
import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.services.DkifService;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static no.nav.syfo.testhelper.OidcTestHelper.loggInnVeileder;
import static no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static no.nav.syfo.testhelper.UserConstants.VEILEDER_ID;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class BrukerControllerTilgangTest extends AbstractControllerTilgangTest {

    @Inject
    private BrukerController brukerRessurs;

    @MockBean
    private BrukerprofilService brukerprofilService;
    @MockBean
    private DkifService dkifService;

    @Test
    public void har_tilgang() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, OK);

        when(brukerprofilService.hentBruker(ARBEIDSTAKER_FNR)).thenReturn(new WSBruker().withPersonnavn(new WSPersonnavn()));
        when(dkifService.hentKontaktinfoFnr(ARBEIDSTAKER_FNR, OIDCIssuer.INTERN)).thenReturn(new Kontaktinfo());

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR);

        brukerRessurs.hentNavn(ARBEIDSTAKER_FNR);
    }

}
