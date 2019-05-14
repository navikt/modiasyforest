package no.nav.syfo.controller;

import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.SykepengesoknaderService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static java.util.Collections.emptyList;
import static no.nav.syfo.testhelper.OidcTestHelper.loggInnVeileder;
import static no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static no.nav.syfo.testhelper.UserConstants.VEILEDER_ID;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class SykepengesoknadControllerTilgangTest extends AbstractControllerTilgangTest {

    private static final String AKTOR_ID = "42";

    @Inject
    private SykepengesoknadController sykepengesoknadController;

    @MockBean
    private SykepengesoknaderService sykepengesoknaderService;
    @MockBean
    private AktorConsumer aktorConsumer;


    @Test
    public void har_tilgang() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, OK);
        when(aktorConsumer.hentAktoerIdForFnr(ARBEIDSTAKER_FNR)).thenReturn(AKTOR_ID);
        when(sykepengesoknaderService.hentSykepengesoknader(AKTOR_ID, OIDCIssuer.INTERN)).thenReturn(emptyList());

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR);

        sykepengesoknadController.hentSykepengesoknader(ARBEIDSTAKER_FNR);
    }

}
