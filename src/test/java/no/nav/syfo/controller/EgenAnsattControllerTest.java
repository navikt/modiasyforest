package no.nav.syfo.controller;

import no.nav.syfo.LocalApplication;
import no.nav.syfo.services.EgenAnsattService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static no.nav.syfo.services.TilgangService.FNR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class EgenAnsattControllerTest {

    @Inject
    private EgenAnsattController egenAnsattController;

    @MockBean
    private EgenAnsattService egenAnsattService;

    @Test
    public void henter_egen_ansatt_flagg() {
        when(egenAnsattService.erEgenAnsatt(FNR)).thenReturn(false);

        EgenAnsattController.EgenAnsattSvar egenAnsattSvar = egenAnsattController.hentErEgenAnsatt(FNR);

        assertThat(egenAnsattSvar.erEgenAnsatt).isEqualTo(false);
    }

}
