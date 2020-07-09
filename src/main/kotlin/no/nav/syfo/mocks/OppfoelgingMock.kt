package no.nav.syfo.mocks

import no.nav.syfo.config.consumer.SykefravaerOppfoelgingConfig
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.*
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.*
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ConditionalOnProperty(value = [SykefravaerOppfoelgingConfig.MOCK_KEY], havingValue = "true")
class OppfoelgingMock : SykefravaersoppfoelgingV1 {
    override fun hentNaermesteLederListe(request: WSHentNaermesteLederListeRequest): WSHentNaermesteLederListeResponse {
        return WSHentNaermesteLederListeResponse().withNaermesteLederListe(listOf(
            WSNaermesteLeder()
                .withNavn("Test Trondsen")
                .withEpost("testTron@nav.no")
                .withOrgnummer("000321000")
                .withMobil("12356772")
                .withArbeidsgiverForskuttererLoenn(true)
                .withNaermesteLederStatus(WSNaermesteLederStatus()
                    .withErAktiv(true)
                    .withAktivFom(LocalDate.now()))
        ))
    }

    override fun ping() {}

    @Throws(HentSykeforlopperiodeSikkerhetsbegrensning::class)
    override fun hentSykeforlopperiode(wsHentSykeforlopperiodeRequest: WSHentSykeforlopperiodeRequest): WSHentSykeforlopperiodeResponse {
        return WSHentSykeforlopperiodeResponse()
            .withSykeforlopperiodeListe(
                WSSykeforlopperiode()
                    .withAktivitet(OPPFOLGINGSTILFELLE_PERIODE_AKTIVITET)
                    .withGrad(OPPFOLGINGSTILFELLE_PERIODE_GRAD)
                    .withFom(OPPFOLGINGSTILFELLE_PERIODE_FOM)
                    .withTom(OPPFOLGINGSTILFELLE_PERIODE_TOM)
            )
    }

    @Throws(HentNaermesteLedersHendelseListeSikkerhetsbegrensning::class)
    override fun hentNaermesteLedersHendelseListe(wsHentNaermesteLedersHendelseListeRequest: WSHentNaermesteLedersHendelseListeRequest): WSHentNaermesteLedersHendelseListeResponse? {
        return null
    }

    @Throws(BerikNaermesteLedersAnsattBolkSikkerhetsbegrensning::class)
    override fun berikNaermesteLedersAnsattBolk(wsBerikNaermesteLedersAnsattBolkRequest: WSBerikNaermesteLedersAnsattBolkRequest): WSBerikNaermesteLedersAnsattBolkResponse? {
        return null
    }

    override fun hentNaermesteLeder(request: WSHentNaermesteLederRequest): WSHentNaermesteLederResponse {
        throw RuntimeException("Ikke implmentert. Se OppfoelgingMock")
    }

    @Throws(HentHendelseListeSikkerhetsbegrensning::class)
    override fun hentHendelseListe(request: WSHentHendelseListeRequest): WSHentHendelseListeResponse? {
        return null
    }

    override fun hentNaermesteLedersAnsattListe(request: WSHentNaermesteLedersAnsattListeRequest): WSHentNaermesteLedersAnsattListeResponse {
        return WSHentNaermesteLedersAnsattListeResponse().withAnsattListe(listOf(
            WSAnsatt()
                .withNaermesteLederStatus(WSNaermesteLederStatus().withAktivFom(LocalDate.now().minusDays(10)).withErAktiv(true))
                .withAktoerId("1112221112221")
                .withNaermesteLederId(345)
                .withNavn("Test Testesen")
                .withOrgnummer("112211221"),
            WSAnsatt()
                .withNaermesteLederStatus(WSNaermesteLederStatus().withAktivTom(LocalDate.now().minusDays(10)).withAktivFom(LocalDate.now().minusDays(20)).withErAktiv(false))
                .withAktoerId("2223332223332")
                .withNaermesteLederId(234)
                .withNavn("Test Testesen")
                .withOrgnummer("223322332"),
            WSAnsatt()
                .withNaermesteLederStatus(WSNaermesteLederStatus().withAktivFom(LocalDate.now().minusDays(10)).withErAktiv(true))
                .withAktoerId("3334443334443")
                .withNaermesteLederId(346)
                .withNavn("Test Testesen")
                .withOrgnummer("334433443")
        ))
    }

    companion object {
        const val OPPFOLGINGSTILFELLE_PERIODE_AKTIVITET = "Aktivitet"
        const val OPPFOLGINGSTILFELLE_PERIODE_GRAD = 80
        val OPPFOLGINGSTILFELLE_PERIODE_FOM = LocalDate.now().minusDays(1)
        val OPPFOLGINGSTILFELLE_PERIODE_TOM = LocalDate.now().plusDays(1)
    }
}
