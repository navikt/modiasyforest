package no.nav.sbl.dialogarena.modiasyforest.controller;

import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.OidcTestHelper.loggInnVeileder;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static no.nav.sbl.dialogarena.modiasyforest.testhelper.UserConstants.VEILEDER_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class SykmeldingControllerTilgangTest extends AbstractControllerTilgangTest {

    private static final String ARBEIDSGIVER = "arbeidsgiver";

    @Inject
    private SykmeldingController sykmeldingController;

    @MockBean
    private SykmeldingService sykmeldingService;

    @Test
    public void har_tilgang() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, OK);
        when(sykmeldingService.hentSykmeldinger(anyString(), anyList(), anyString())).thenReturn(emptyList());

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR);

        sykmeldingController.hentSykmeldinger(ARBEIDSTAKER_FNR, ARBEIDSGIVER);
    }
}
