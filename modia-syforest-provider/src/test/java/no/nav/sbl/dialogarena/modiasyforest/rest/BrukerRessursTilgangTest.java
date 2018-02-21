package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo;
import no.nav.sbl.dialogarena.modiasyforest.services.BrukerprofilService;
import no.nav.sbl.dialogarena.modiasyforest.services.DkifService;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BrukerRessursTilgangTest extends AbstractRessursTilgangTest {

    @Mock
    private BrukerprofilService brukerprofilService;
    @Mock
    private DkifService dkifService;

    @InjectMocks
    private BrukerRessurs brukerRessurs;

    @Test
    public void historikk_har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(brukerprofilService.hentBruker(FNR)).thenReturn(new WSBruker().withPersonnavn(new WSPersonnavn()));
        when(dkifService.hentKontaktinfoFnr(FNR)).thenReturn(new Kontaktinfo());

        brukerRessurs.hentNavn(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void historikk_har_ikke_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);

        brukerRessurs.hentNavn(FNR);
    }

    @Test(expected = WebApplicationException.class)
    public void historikk_annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        brukerRessurs.hentNavn(FNR);
    }


}