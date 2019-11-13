package no.nav.syfo.controller.internad;

import no.nav.syfo.LocalApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static no.nav.syfo.mocks.DiskresjonskodeMock.DISKRESJONSKODE;
import static no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
public class DiskresjonskodeADControllerTest {

    @Inject
    private DiskresjonskodeADController diskresjonskodeADController;

    @Test
    public void getDiskresjonskode() {
        DiskresjonskodeADController.DiskresjonskodeSvar diskresjonskodeSvar = diskresjonskodeADController.getDiskresjonskode(ARBEIDSTAKER_FNR);

        assertThat(diskresjonskodeSvar.diskresjonskode).isEqualTo(DISKRESJONSKODE);
    }
}
