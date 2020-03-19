package no.nav.syfo.controller.internad;

import no.nav.syfo.LocalApplication;
import no.nav.syfo.controller.AbstractControllerTilgangTest;
import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.dkif.DigitalKontaktinfo;
import no.nav.syfo.dkif.DkifConsumer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.text.ParseException;

import static no.nav.syfo.testhelper.DKIFKontakinformasjonResponseGeneratorKt.generateDigitalKontaktinfo;
import static no.nav.syfo.testhelper.OidcTestHelper.logInVeilederAD;
import static no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle;
import static no.nav.syfo.testhelper.UserConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
public class UserControllerTest extends AbstractControllerTilgangTest {

    @MockBean
    private DkifConsumer dkifConsumer;

    @Inject
    private UserController userController;

    @Before
    public void setup() throws ParseException {
        logInVeilederAD(oidcRequestContextHolder, VEILEDER_ID);
    }

    @After
    public void tearDown() {
        loggUtAlle(oidcRequestContextHolder);
    }

    @Test
    public void getUserHasAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, OK);

        DigitalKontaktinfo digitalKontaktinfo = generateDigitalKontaktinfo();
        when(dkifConsumer.kontaktinformasjon(ARBEIDSTAKER_FNR)).thenReturn(digitalKontaktinfo);

        Bruker user = userController.getUser(ARBEIDSTAKER_FNR);

        assertEquals(PERSON_NAVN, user.navn);
    }

    @Test(expected = ForbiddenException.class)
    public void getUserNoAccess() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, FORBIDDEN);

        userController.getUser(ARBEIDSTAKER_FNR);
    }

    @Test(expected = RuntimeException.class)
    public void getUserServerErrror() {
        mockSvarFraTilgangTilBrukerViaAzure(ARBEIDSTAKER_FNR, INTERNAL_SERVER_ERROR);

        userController.getUser(ARBEIDSTAKER_FNR);
    }
}
