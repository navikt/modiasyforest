package no.nav.syfo.controller

import no.nav.syfo.LocalApplication
import no.nav.syfo.mocks.DiskresjonskodeMock.DISKRESJONSKODE
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class DiskresjonskodeADControllerTest {

    @Inject
    private lateinit var diskresjonskodeADController: DiskresjonskodeADController

    @Test
    fun getDiskresjonskode() {
        val diskresjonskodeSvar = diskresjonskodeADController.getDiskresjonskode(ARBEIDSTAKER_FNR)

        assertThat(diskresjonskodeSvar.diskresjonskode).isEqualTo(DISKRESJONSKODE)
    }
}
