package no.nav.syfo.consumer

import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.ereg.EregConsumer
import no.nav.syfo.ereg.Virksomhetsnummer
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederStatus
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeResponse
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NaermesteLederConsumerTest {
    @Mock
    private lateinit var sykefravaersoppfoelgingV1: SykefravaersoppfoelgingV1

    @Mock
    private lateinit var aktorConsumer: AktorConsumer

    @Mock
    private lateinit var eregConsumer: EregConsumer

    @InjectMocks
    private lateinit var naermesteLederConsumer: NaermesteLederConsumer

    @Before
    fun setup() {
        Mockito.`when`(aktorConsumer.hentAktoerIdForFnr(ArgumentMatchers.anyString())).thenReturn("12345678901")
        Mockito.`when`(eregConsumer.virksomhetsnavn(Virksomhetsnummer("123456789"))).thenReturn("Testbedriften")
        Mockito.`when`(eregConsumer.virksomhetsnavn(Virksomhetsnummer("123456782"))).thenReturn("Testbedriften")
    }

    @Test
    @Throws(HentNaermesteLederListeSikkerhetsbegrensning::class)
    fun henterNaermesteledere() {
        Mockito.`when`(sykefravaersoppfoelgingV1.hentNaermesteLederListe(ArgumentMatchers.any())).thenReturn(WSHentNaermesteLederListeResponse().withNaermesteLederListe(listOf(
            WSNaermesteLeder()
                .withNaermesteLederStatus(WSNaermesteLederStatus()
                    .withErAktiv(true))
                .withMobil("123")
                .withNavn("Navn")
                .withEpost("test@nav.no")
                .withOrgnummer("123456789"),
            WSNaermesteLeder()
                .withNaermesteLederStatus(WSNaermesteLederStatus()
                    .withErAktiv(true))
                .withMobil("123")
                .withNavn("Navn")
                .withEpost("test@nav.no")
                .withOrgnummer("123456789"),
            WSNaermesteLeder()
                .withNaermesteLederStatus(WSNaermesteLederStatus()
                    .withErAktiv(true))
                .withMobil("321")
                .withNavn("Navn2")
                .withEpost("test2@nav.no")
                .withOrgnummer("123456782")
        )))
        val naermesteledere = naermesteLederConsumer.hentNaermesteledere("12345678901")
        //Henter distincte innslag
        Assertions.assertThat(naermesteledere.size).isEqualTo(2)
        Assertions.assertThat(naermesteledere[0].navn).isEqualTo("Navn")
        Assertions.assertThat(naermesteledere[0].organisasjonsnavn).isEqualTo("Testbedriften")
    }

    @Test
    fun hentOrgSomIkkeHarsendtNaermesteLeder() {
        val naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), listOf(
            Sykmelding()
                .withOrgnummer("2")
                .withStatus("SENDT")
        ))
        Assertions.assertThat(naermesteledere.size).isEqualTo(1)
    }

    @Test
    fun hvisOrgHarSendtBlirIkkeSykmeldingenMed() {
        val naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(listOf(
            NaermesteLeder().withOrgnummer("1")
        ), listOf(
            Sykmelding()
                .withOrgnummer("1")
                .withStatus("SENDT")
        ))
        Assertions.assertThat(naermesteledere.size).isEqualTo(0)
    }

    @Test
    fun ikkeSendteBlirIkkeMed() {
        val naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), listOf(
            Sykmelding()
                .withOrgnummer("1")
                .withStatus("NY")
        ))
        Assertions.assertThat(naermesteledere.size).isEqualTo(0)
    }

    @Test
    fun multipleSendteSykmeldingerTilSammeArbeidsgiverBlirBareEn() {
        val naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), listOf(
            Sykmelding()
                .withOrgnummer("1")
                .withStatus("SENDT"),
            Sykmelding()
                .withOrgnummer("1")
                .withStatus("SENDT")
        ))
        Assertions.assertThat(naermesteledere.size).isEqualTo(1)
    }
}