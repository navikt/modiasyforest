package no.nav.syfo.controller

import no.nav.syfo.LocalApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

import javax.inject.Inject

import no.nav.syfo.mocks.EgenansattMock.IS_EGEN_ANSATT
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import org.assertj.core.api.Assertions.assertThat

@DirtiesContext
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
class EgenAnsattADControllerTest {

    @Inject
    private lateinit var egenAnsattController: EgenAnsattADController

    @Test
    fun getIsEgenAnsatt() {
        val egenAnsattSvar = egenAnsattController.getIsEgenAnsatt(ARBEIDSTAKER_FNR)

        assertThat(egenAnsattSvar.erEgenAnsatt).isEqualTo(IS_EGEN_ANSATT)
    }
}
