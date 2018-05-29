package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.Tilgang;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.ForbiddenException;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tilgang.AdRoller.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SykmeldingRessursTilgangTest extends AbstractRessursTilgangTest {

    private static final String ARBEIDSGIVER = "arbeidsgiver";

    @Mock
    private SykmeldingService sykmeldingService;

    @InjectMocks
    private SykmeldingRessurs sykmeldingRessurs;

    @Test
    public void har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(sykmeldingService.hentSykmeldinger(anyString(), anyListOf(WSSkjermes.class))).thenReturn(emptyList());

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);

        verify(tilgangskontrollResponse).getStatus();
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_6() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().begrunnelse(KODE6.name()));

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_kode_7() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().begrunnelse(KODE7.name()));

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_egenansatt() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().begrunnelse(EGEN_ANSATT.name()));

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);
    }

    @Test(expected = ForbiddenException.class)
    public void har_ikke_tilgang_sensitiv() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(403);
        when(tilgangskontrollResponse.readEntity(Tilgang.class)).thenReturn(new Tilgang().begrunnelse(SYFO.name()));

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);
    }

    @Test(expected = RuntimeException.class)
    public void annen_tilgangsfeil() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(500);
        when(tilgangskontrollResponse.getStatusInfo()).thenReturn(Statuses.from(500, "Tau i propellen"));

        sykmeldingRessurs.hentSykmeldinger(FNR, ARBEIDSGIVER);
    }

}