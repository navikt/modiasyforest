package no.nav.sbl.dialogarena.modiasyforest.controller;

import no.nav.sbl.dialogarena.modiasyforest.LocalApplication;
import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiasyforest.testhelper.UserConstants.ARBEIDSTAKER_FNR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class DiskresjonskodeControllerTest {

    @Inject
    private DiskresjonskodeController diskresjonskodeController;

    @MockBean
    private DiskresjonskodeService diskresjonskodeService;

    @Test
    public void henter_diskresjonskode() {
        when(diskresjonskodeService.diskresjonskode(ARBEIDSTAKER_FNR)).thenReturn("42");

        DiskresjonskodeController.DiskresjonskodeSvar diskresjonskodeSvar = diskresjonskodeController.hentDiskresjonskode(ARBEIDSTAKER_FNR);

        assertThat(diskresjonskodeSvar.diskresjonskode).isEqualTo("42");
    }

}
