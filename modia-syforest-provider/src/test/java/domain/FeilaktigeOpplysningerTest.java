package domain;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.FeilaktigeOpplysninger;
import no.nav.tjeneste.virksomhet.behandle.sykmelding.v1.WSFeilaktigOpplysning;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class FeilaktigeOpplysningerTest {
    @Test
    public void tilWSFeilaktigeOpplysningerListe() throws Exception {
        FeilaktigeOpplysninger f = new FeilaktigeOpplysninger();
        f.diagnose = true;
        f.periode = false;

        List<WSFeilaktigOpplysning> wsFeilaktigOpplysnings = f.tilWSFeilaktigeOpplysningerListe();
        assertThat(wsFeilaktigOpplysnings.size()).isEqualTo(1);
        assertThat(wsFeilaktigOpplysnings.contains(WSFeilaktigOpplysning.DIAGNOSE));
    }

}