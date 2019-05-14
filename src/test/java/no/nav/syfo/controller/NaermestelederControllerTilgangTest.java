package no.nav.syfo.controller;

import no.nav.syfo.controller.domain.NaermesteLeder;
import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.services.NaermesteLederService;
import no.nav.syfo.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static java.util.Collections.emptyList;
import static no.nav.syfo.testhelper.OidcTestHelper.loggInnVeileder;
import static no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static no.nav.syfo.testhelper.UserConstants.VEILEDER_ID;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class NaermestelederControllerTilgangTest extends AbstractControllerTilgangTest {

    @Inject
    private NaermestelederController naermestelederController;

    @MockBean
    private SykmeldingService sykmeldingService;
    @MockBean
    private NaermesteLederService naermesteLederService;

    @Test
    public void har_tilgang() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, OK);

        when(sykmeldingService.hentSykmeldinger(anyString(), anyListOf(WSSkjermes.class), anyString())).thenReturn(emptyList());
        when(naermesteLederService.hentNaermesteledere(ARBEIDSTAKER_FNR)).thenReturn(emptyList());
        when(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(anyListOf(NaermesteLeder.class), anyListOf(Sykmelding.class))).thenReturn(emptyList());

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, FORBIDDEN);

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockSvarFraTilgangskontroll(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR);

        naermestelederController.hentNaermesteledere(ARBEIDSTAKER_FNR);
    }
}
