package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.AktoerService;
import no.nav.sbl.dialogarena.modiasyforest.services.SykepengesoknaderService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SykepengesoknadRessursTilgangTest extends AbstractRessursTilgangTest {

    private static final String AKTOR_ID = "42";

    @Mock
    private SykepengesoknaderService sykepengesoknaderService;
    @Mock
    private AktoerService aktoerService;

    @InjectMocks
    private SykepengesoknadRessurs sykepengesoknadRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(aktoerService.hentAktoerIdForFnr(FNR)).thenReturn(AKTOR_ID);
        when(sykepengesoknaderService.hentSykepengesoknader(AKTOR_ID)).thenReturn(emptyList());

        sykepengesoknadRessurs.hentSykepengesoknader(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);

        sykepengesoknadRessurs.hentSykepengesoknader(FNR);
    }

    @Test(expected = WebApplicationException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        sykepengesoknadRessurs.hentSykepengesoknader(FNR);
    }

}