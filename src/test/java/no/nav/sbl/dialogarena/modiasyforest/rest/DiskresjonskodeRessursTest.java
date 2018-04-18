package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.services.DiskresjonskodeService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class DiskresjonskodeRessursTest extends AbstractRessursTilgangTest {

    @Mock
    private DiskresjonskodeService diskresjonskodeService;

    @InjectMocks
    private DiskresjonskodeRessurs diskresjonskodeRessurs;

    @Test
    public void henter_diskresjonskode() {
        when(diskresjonskodeService.diskresjonskode(FNR)).thenReturn("42");

        DiskresjonskodeRessurs.DiskresjonskodeSvar diskresjonskodeSvar = diskresjonskodeRessurs.hentDiskresjonskode(FNR);

        assertThat(diskresjonskodeSvar.diskresjonskode).isEqualTo("42");
    }

}