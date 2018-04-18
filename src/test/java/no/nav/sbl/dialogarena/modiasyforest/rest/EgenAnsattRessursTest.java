package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.EgenAnsattService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EgenAnsattRessursTest extends AbstractRessursTilgangTest {

    @Mock
    private EgenAnsattService egenAnsattService;

    @InjectMocks
    private EgenAnsattRessurs egenAnsattRessurs;

    @Test
    public void henter_egen_ansatt_flagg() {
        when(egenAnsattService.erEgenAnsatt(FNR)).thenReturn(false);

        EgenAnsattRessurs.EgenAnsattSvar egenAnsattSvar = egenAnsattRessurs.hentErEgenAnsatt(FNR);

        assertThat(egenAnsattSvar.erEgenAnsatt).isEqualTo(false);
    }

}