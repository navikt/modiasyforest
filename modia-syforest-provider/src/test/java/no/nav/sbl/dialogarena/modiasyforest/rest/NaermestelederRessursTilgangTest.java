package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NaermestelederRessursTilgangTest extends AbstractRessursTilgangTest {

    @Mock
    private SykmeldingService sykmeldingService;
    @Mock
    private NaermesteLederService naermesteLederService;

    @InjectMocks
    private NaermestelederRessurs naermestelederRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(sykmeldingService.hentSykmeldinger(anyString(), anyListOf(WSSkjermes.class))).thenReturn(emptyList());
        when(naermesteLederService.hentNaermesteledere(FNR)).thenReturn(emptyList());
        when(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(anyListOf(NaermesteLeder.class), anyListOf(Sykmelding.class))).thenReturn(emptyList());

        naermestelederRessurs.hentNaermesteledere(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);

        naermestelederRessurs.hentNaermesteledere(FNR);
    }

    @Test(expected = WebApplicationException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        naermestelederRessurs.hentNaermesteledere(FNR);
    }

}