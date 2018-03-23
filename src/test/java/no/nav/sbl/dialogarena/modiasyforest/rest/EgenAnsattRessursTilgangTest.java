package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import no.nav.sbl.dialogarena.modiasyforest.services.EgenAnsattService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;

import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EgenAnsattRessursTilgangTest extends AbstractRessursTilgangTest {

    @Mock
    private EgenAnsattService egenAnsattService;

    @InjectMocks
    private EgenAnsattRessurs egenAnsattRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(egenAnsattService.erEgenAnsatt(FNR)).thenReturn(false);

        egenAnsattRessurs.hentErEgenAnsatt(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE6.name()));

        egenAnsattRessurs.hentErEgenAnsatt(FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE7.name()));

        egenAnsattRessurs.hentErEgenAnsatt(FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(EGEN_ANSATT.name()));

        egenAnsattRessurs.hentErEgenAnsatt(FNR);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(SYFO.name()));

        egenAnsattRessurs.hentErEgenAnsatt(FNR);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        egenAnsattRessurs.hentErEgenAnsatt(FNR);
    }

}