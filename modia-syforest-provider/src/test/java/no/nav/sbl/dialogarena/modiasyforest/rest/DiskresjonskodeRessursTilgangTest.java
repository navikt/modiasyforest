package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoTilgangException;
import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiskresjonskodeRessursTilgangTest extends AbstractRessursTilgangTest {

    @Mock
    private DiskresjonskodeService diskresjonskodeService;

    @InjectMocks
    private DiskresjonskodeRessurs diskresjonskodeRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(diskresjonskodeService.diskresjonskode(FNR)).thenReturn("42");

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_kode_6() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE6.name()));

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_kode_7() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(KODE7.name()));

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_egenansatt() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(EGEN_ANSATT.name()));

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);
    }

    @Test(expected = SyfoTilgangException.class)
    public void har_ikke_tilgang_sensitiv() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().ikkeTilgangGrunn(SYFO.name()));

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);
    }

    @Test(expected = SyfoException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        diskresjonskodeRessurs.hentDiskresjonskode(FNR);
    }

}