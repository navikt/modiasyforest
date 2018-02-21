package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TidslinjeRessursTilgangTest extends AbstractRessursTilgangTest{

    private static final String TYPE = "type";

    @Mock
    private TidslinjeService tidslinjeService;

    @InjectMocks
    private TidslinjeRessurs tidslinjeRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(tidslinjeService.hentTidslinjer(FNR, TYPE)).thenReturn(emptyList());

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

    @Test(expected = WebApplicationException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

}