package no.nav.syfo.consumer

import no.nav.syfo.services.BrukerprofilService
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BrukerprofilServiceTest {
    @Mock
    private lateinit var brukerprofilV3: BrukerprofilV3

    @InjectMocks
    private lateinit var brukerprofilService: BrukerprofilService

    @Test
    @Throws(HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning::class, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt::class, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet::class)
    fun captitalizerNavnet() {
        Mockito.`when`(brukerprofilV3.hentKontaktinformasjonOgPreferanser(ArgumentMatchers.any())).thenReturn(WSHentKontaktinformasjonOgPreferanserResponse().withBruker(WSBruker().withPersonnavn(WSPersonnavn().withFornavn("TROND-VIGGO").withEtternavn("TESTESEN"))))
        val bruker = brukerprofilService.hentBruker("12345678901")
        val navn = bruker.navn
        Assertions.assertThat(navn).isEqualTo("Trond-Viggo Testesen")
    }

    @Test(expected = RuntimeException::class)
    @Throws(HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning::class, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt::class, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet::class)
    fun taklerIkkeStreng() {
        brukerprofilService.hentBruker("mote")
    }

    @Test(expected = RuntimeException::class)
    @Throws(HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning::class, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt::class, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet::class)
    fun maVaereIdentMedLengde11() {
        brukerprofilService.hentBruker("123")
    }
}
