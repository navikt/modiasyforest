package no.nav.syfo.mocks

import no.nav.syfo.config.consumer.SykmeldingerConfig
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@ConditionalOnProperty(value = [SykmeldingerConfig.MOCK_KEY], havingValue = "true")
class SykmeldingV1Mock : SykmeldingV1 {
    override fun hentNaermesteLedersSykmeldingListe(wsHentNaermesteLedersSykmeldingListeRequest: WSHentNaermesteLedersSykmeldingListeRequest): WSHentNaermesteLedersSykmeldingListeResponse? {
        return null
    }

    override fun hentSykmeldingListe(request: WSHentSykmeldingListeRequest): WSHentSykmeldingListeResponse? {
        return try {
            val ny = WSMelding()
                .withMeldingId("2")
                .withIdentdato(LocalDate.now().minusDays(10))
                .withStatus("NY")
                .withSkalSkjermesForPasient(false)
                .withSykmelding(WSSykmelding()
                    .withArbeidsgiver(WSArbeidsgiver()
                        .withNavn("NAV Consulting AS")
                        .withStillingsprosent(100))
                    .withBehandler(WSBehandler()
                        .withNavn(WSNavn()
                            .withFornavn("Lars")
                            .withEtternavn("Legesen"))
                        .withKontaktinformasjon("22334455"))
                    .withPasient(WSPasient()
                        .withFnr("01010199999")
                        .withNavn(WSNavn()
                            .withFornavn("Test")
                            .withEtternavn("Testesen")))
                    .withMedisinskVurdering(WSMedisinskVurdering()
                        .withHoveddiagnose(WSDiagnose()
                            .withValue("Skikkelig syk i kneet"))
                        .withBidiagnoser(listOf(
                            WSDiagnose()
                                .withValue("Bidiagnose 1")
                                .withKodeRef("kodeverk1"),
                            WSDiagnose()
                                .withValue("Bidiagnose 2")
                                .withKodeRef("kodeverk2"),
                            WSDiagnose()
                                .withValue("Bidiagnose 3")
                                .withKodeRef("kodeverk3")
                        ))
                        .withAnnenFravaersaarsak(WSAarsak().withAarsaker(WSAarsaker().withValue("Ingen (h)års(m)ak"))
                            .withBeskrivelse("Beskrivelse av fravær")))
                    .withUtdypendeOpplysninger(WSUtdypendeOpplysninger()
                        .withSpoersmaal(WSSpoersmaal().withSpoersmaalId("6.2.1")
                            .withSvar("jatakk, begge deler")))
                    .withPrognose(WSPrognose()
                        .withErArbeidsfoerEtterEndtPeriode(true)
                        .withArbeidsutsikter(WSArbeidsutsikter().withErIArbeid(WSErIArbeid()
                            .withHarEgetArbeidPaaSikt(true)
                            .withArbeidFom(LocalDate.now().plusDays(8)))))
                    .withMeldingTilNav(WSMeldingTilNav()
                        .withTrengerBistandFraNavUmiddelbart(true))
                    .withKontaktMedPasient(WSKontaktMedPasient()
                        .withBehandlet(LocalDateTime.now().minusDays(11)))
                    .withSyketilfelleFom(LocalDate.now().minusDays(10))
                    .withPerioder(perioderWS())
                )
            WSHentSykmeldingListeResponse()
                .withMeldingListe(meldingSykmeldingSendt, ny)
        } catch (e: Exception) {
            null
        }
    }

    override fun hentOppfoelgingstilfelleListe(wsHentOppfoelgingstilfelleListeRequest: WSHentOppfoelgingstilfelleListeRequest): WSHentOppfoelgingstilfelleListeResponse {
        return WSHentOppfoelgingstilfelleListeResponse()
            .withOppfoelgingstilfelleListe(oppfoelgingstilfelle)
    }

    override fun ping() {}
    private val meldingSykmeldingSendt = WSMelding()
        .withMeldingId("1")
        .withArbeidsgiver("000123000")
        .withIdentdato(LocalDate.now().minusDays(10))
        .withSendtTilArbeidsgiverDato(LocalDateTime.now().minusDays(9))
        .withStatus("SENDT")
        .withSkalSkjermesForPasient(false)
        .withSykmelding(WSSykmelding()
            .withArbeidsgiver(WSArbeidsgiver()
                .withNavn("NAV Consulting AS")
                .withStillingsprosent(100))
            .withBehandler(WSBehandler()
                .withNavn(WSNavn()
                    .withFornavn("Lars")
                    .withEtternavn("Legesen"))
                .withKontaktinformasjon("22334455"))
            .withPasient(WSPasient()
                .withFnr("01010199999")
                .withNavn(WSNavn()
                    .withFornavn("Test")
                    .withMellomnavn("Mellomnavn")
                    .withEtternavn("Testesen")))
            .withMedisinskVurdering(WSMedisinskVurdering()
                .withHoveddiagnose(WSDiagnose()
                    .withValue("Skikkelig syk i kneet"))
                .withBidiagnoser(listOf(
                    WSDiagnose()
                        .withValue("Bidiagnose 1")
                        .withKodeRef("kodeverk1"),
                    WSDiagnose()
                        .withValue("Bidiagnose 2")
                        .withKodeRef("kodeverk2"),
                    WSDiagnose()
                        .withValue("Bidiagnose 3")
                        .withKodeRef("kodeverk3")
                ))
                .withAnnenFravaersaarsak(WSAarsak().withAarsaker(WSAarsaker().withValue("Ingen (h)års(m)ak"))
                    .withBeskrivelse("Beskrivelse av fravær")))
            .withUtdypendeOpplysninger(WSUtdypendeOpplysninger()
                .withSpoersmaal(WSSpoersmaal().withSpoersmaalId("6.2.1")
                    .withSvar("jatakk, begge deler")))
            .withPrognose(WSPrognose()
                .withErArbeidsfoerEtterEndtPeriode(true)
                .withArbeidsutsikter(WSArbeidsutsikter().withErIArbeid(WSErIArbeid()
                    .withHarEgetArbeidPaaSikt(true)
                    .withArbeidFom(LocalDate.now().plusDays(8)))))
            .withMeldingTilNav(WSMeldingTilNav()
                .withTrengerBistandFraNavUmiddelbart(true))
            .withKontaktMedPasient(WSKontaktMedPasient()
                .withBehandlet(LocalDateTime.now().minusDays(11)))
            .withSyketilfelleFom(LocalDate.now().minusDays(10))
            .withPerioder(perioderWS())
        )
    val oppfoelgingstilfelle = WSOppfoelgingstilfelle()
        .withHendelseListe(emptyList())
        .withMeldingListe(meldingSykmeldingSendt)
        .withOppfoelgingsdato(LocalDate.now())

    @Throws(Exception::class)
    private fun perioderWS(): List<WSPeriode> {
        return listOf(
            WSPeriode()
                .withFom(LocalDate.now().minusDays(9))
                .withTom(LocalDate.now().plusDays(11))
                .withAktivitet(WSAktivitet()
                    .withAktivitetIkkeMulig(WSAktivitetIkkeMulig()
                        .withMedisinskeAarsaker(WSAarsak()
                            .withBeskrivelse("Årsaken er beskrevet")
                            .withAarsaker(listOf(
                                WSAarsaker()
                                    .withValue("Årsak 1"),
                                WSAarsaker()
                                    .withValue("Årsak 2")
                            ))))),
            WSPeriode()
                .withFom(LocalDate.now().plusDays(11))
                .withTom(LocalDate.now().plusDays(12))
                .withAktivitet(WSAktivitet()
                    .withGradertSykmelding(WSGradertSykmelding()
                        .withSykmeldingsgrad(50))
                ),
            WSPeriode()
                .withFom(LocalDate.now().plusDays(15))
                .withTom(LocalDate.now().plusDays(19))
                .withAktivitet(WSAktivitet()
                    .withAntallBehandlingsdagerUke(5)),
            WSPeriode()
                .withFom(LocalDate.now().plusDays(22))
                .withTom(LocalDate.now().plusDays(29))
                .withAktivitet(WSAktivitet()
                    .withHarReisetilskudd(true)),
            WSPeriode()
                .withFom(LocalDate.now().plusDays(29))
                .withTom(LocalDate.now().plusDays(30))
                .withAktivitet(WSAktivitet()
                    .withAvventendeSykmelding("Skikkelig avventende"))
        )
    }
}
