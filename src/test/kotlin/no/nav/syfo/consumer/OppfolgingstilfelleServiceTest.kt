package no.nav.syfo.consumer

import no.nav.syfo.LocalApplication
import no.nav.syfo.oidc.OIDCIssuer
import no.nav.syfo.services.OppfolgingstilfelleService
import no.nav.syfo.testhelper.SykmeldingMocks.getWSSykmelding
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class OppfolgingstilfelleServiceTest {
    @Inject
    private lateinit var oppfolgingstilfelleService: OppfolgingstilfelleService

    @MockBean
    private lateinit var aktorConsumer: AktorConsumer

    @MockBean
    private lateinit var sykmeldingV1: SykmeldingV1

    @MockBean
    private lateinit var naermesteLederConsumer: NaermesteLederConsumer

    @Test
    @Throws(Exception::class)
    fun hentSykeforloep() {
        Mockito.`when`(sykmeldingV1.hentOppfoelgingstilfelleListe(ArgumentMatchers.any())).thenReturn(
            WSHentOppfoelgingstilfelleListeResponse()
                .withOppfoelgingstilfelleListe(
                    listOf(
                        WSOppfoelgingstilfelle()
                            .withOppfoelgingsdato(LocalDate.now())
                            .withHendelseListe(
                                listOf(
                                    WSHendelse()
                                        .withType(WSHendelsestype.AKTIVITETSKRAV_VARSEL)
                                        .withTidspunkt(LocalDateTime.now())))
                            .withMeldingListe(listOf(
                                WSMelding()
                                    .withSykmelding(getWSSykmelding())
                            )))
                ))
        val sykeforloep = oppfolgingstilfelleService.getOppfolgingstilfelle("12345678901", OIDCIssuer.AZURE)
        Assertions.assertThat(sykeforloep[0].oppfoelgingsdato).isEqualTo(LocalDate.now())
        Assertions.assertThat(sykeforloep[0].hendelser.size).isEqualTo(1)
        Assertions.assertThat(sykeforloep[0].sykmeldinger.size).isEqualTo(1)
    }
}
