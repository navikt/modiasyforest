package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.TilgangService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Hensikten her er å samle koden som mock svar fra syfo-tilgangskontroll.
 * Subklasser arver tilgangskontrollResponse, som de kan sette opp til å returnere 200 OK, 403 Forbidden eller
 * 500-feil.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientBuilder.class)
public abstract class AbstractRessursTilgangTest {

    static final String FNR = "123456789";

    private static Client client;
    private static TilgangService tilgangService;

    @Mock
    Response tilgangskontrollResponse;

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
        //System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());

        // Mock REST-klienten
        Invocation.Builder builderMock = mock(Invocation.Builder.class);
        when(builderMock.get()).thenReturn(tilgangskontrollResponse);
        when(builderMock.header(anyString(), anyString())).thenReturn(builderMock);

        final WebTarget webTargetMock = mock(WebTarget.class);
        when(webTargetMock.request(APPLICATION_JSON)).thenReturn(builderMock);
        when(webTargetMock.queryParam(anyString(), anyString())).thenReturn(webTargetMock);

        when(client.target(anyString())).thenReturn(webTargetMock);
    }

}