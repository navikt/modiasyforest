package no.nav.syfo.mocks

import no.nav.syfo.config.consumer.TpsConfig
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [TpsConfig.MOCK_KEY], havingValue = "true")
class BrukerprofilMock : BrukerprofilV3 {
    override fun ping() {}

    @Throws(HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning::class, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet::class, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt::class)
    override fun hentKontaktinformasjonOgPreferanser(request: WSHentKontaktinformasjonOgPreferanserRequest): WSHentKontaktinformasjonOgPreferanserResponse {
        return WSHentKontaktinformasjonOgPreferanserResponse()
            .withBruker(WSBruker()
                .withPersonnavn(WSPersonnavn()
                    .withFornavn(PERSON_FORNAVN)
                    .withEtternavn(PERSON_ETTERNAVN)))
    }

    companion object {
        const val PERSON_FORNAVN = "Fornavn"
        const val PERSON_ETTERNAVN = "Etternavn"
    }
}
