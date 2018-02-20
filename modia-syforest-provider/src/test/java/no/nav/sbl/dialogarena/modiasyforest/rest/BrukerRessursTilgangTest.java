package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo;
import no.nav.sbl.dialogarena.modiasyforest.services.BrukerprofilService;
import no.nav.sbl.dialogarena.modiasyforest.services.DkifService;
import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientBuilder.class)
public class BrukerRessursTilgangTest {

    private static final String FNR = "123456789";

    private static Client client;
    private static TilgangService tilgangService;

    @Mock
    private BrukerprofilService brukerprofilService;
    @Mock
    private DkifService dkifService;
    @Mock
    private Response tilgangskontrollResponse;

    @InjectMocks
    private BrukerRessurs brukerRessurs;

    @BeforeClass
    public static void initialize() {
        mockStatic(ClientBuilder.class);
        client = mock(Client.class);
        when(ClientBuilder.newClient()).thenReturn(client);
        tilgangService = spy(new TilgangService());
    }

    @Before
    public void setUp() {
        // Sett opp test-subjecthandler
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());

        // Mock REST-klienten
        Invocation.Builder builderMock = mock(Invocation.Builder.class);
        when(builderMock.get()).thenReturn(tilgangskontrollResponse);
        when(builderMock.header(anyString(), anyString())).thenReturn(builderMock);

        final WebTarget webTargetMock = mock(WebTarget.class);
        when(webTargetMock.request(APPLICATION_JSON)).thenReturn(builderMock);
        when(webTargetMock.queryParam(anyString(), anyString())).thenReturn(webTargetMock);

        when(client.target(anyString())).thenReturn(webTargetMock);
    }

    @Test
    public void historikk_har_tilgang() {
        when(tilgangskontrollResponse.getStatus()).thenReturn(200);
        when(brukerprofilService.hentBruker(FNR)).thenReturn(new WSBruker().withPersonnavn(new WSPersonnavn()));
        when(dkifService.hentKontaktinfoFnr(FNR)).thenReturn(new Kontaktinfo());

        brukerRessurs.hentNavn(FNR);

        Mockito.verify(tilgangskontrollResponse).getStatus();
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