package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoTilgangException;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;
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

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_kode_6() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE6.name()));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_kode_7() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE7.name()));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_egenansatt() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(EGEN_ANSATT.name()));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_sensitiv() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(SYFO.name()));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

    @Test(expected = SyfoException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        tidslinjeRessurs.hentTidslinje(FNR, TYPE);
    }

}