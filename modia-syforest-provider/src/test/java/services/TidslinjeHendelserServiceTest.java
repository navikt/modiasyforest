package services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeHendelserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType.MED_ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType.UTEN_ARBEIDSGIVER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TidslinjeHendelserServiceTest {

    @InjectMocks
    TidslinjeHendelserService tidslinjeHendelserService;

    @Test
    public void hentHendelser() throws Exception {
        List<Hendelse> hendelserMedArb = tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER);
        List<Hendelse> hendelserUtenArb = tidslinjeHendelserService.hentHendelser(UTEN_ARBEIDSGIVER);

        assertThat(hendelserMedArb.size()).isEqualTo(8);
        assertThat(hendelserUtenArb.size()).isEqualTo(6);
    }
}