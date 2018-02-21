package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SykeforloepRessursTilgangTest extends AbstractRessursTilgangTest {

    @Mock
    private SykeforloepService sykeforloepService;

    @InjectMocks
    private SykeforloepRessurs sykeforloepRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(sykeforloepService.hentSykeforloep(FNR)).thenReturn(emptyList());

        sykeforloepRessurs.hentOppfoelgingstilfeller(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);

        sykeforloepRessurs.hentOppfoelgingstilfeller(FNR);
    }

    @Test(expected = WebApplicationException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        sykeforloepRessurs.hentOppfoelgingstilfeller(FNR);
    }

}