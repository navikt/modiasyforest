package no.nav.syfo.testhelper

import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object SykmeldingMocks {
    @JvmStatic
    @Throws(Exception::class)
    fun getWSSykmelding(): WSSykmelding {
        return WSSykmelding()
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
                .withAnnenFravaersaarsak(WSAarsak().withAarsaker(WSAarsaker().withValue("Ingen (h)års(m)ak"))
                    .withBeskrivelse("Beskrivelse av fravær")))
            .withUtdypendeOpplysninger(WSUtdypendeOpplysninger()
                .withSpoersmaal(WSSpoersmaal().withSpoersmaalId("6.2.1")
                    .withSvar("jatakk, begge deler")))
            .withPrognose(WSPrognose()
                .withErArbeidsfoerEtterEndtPeriode(true)
                .withArbeidsutsikter(WSArbeidsutsikter().withErIArbeid(WSErIArbeid()
                    .withHarEgetArbeidPaaSikt(true)
                    .withArbeidFom(LocalDate.of(2016, 4, 4)))))
            .withMeldingTilNav(WSMeldingTilNav()
                .withTrengerBistandFraNavUmiddelbart(true))
            .withKontaktMedPasient(WSKontaktMedPasient()
                .withBehandlet(LocalDateTime.of(2016, 1, 1, 12, 12)))
            .withSyketilfelleFom(LocalDate.of(2016, 2, 1))
            .withPerioder(perioderWS())
    }

    @Throws(Exception::class)
    fun perioderWS(): List<WSPeriode> {
        return Arrays.asList(
            WSPeriode()
                .withFom(LocalDate.of(2016, 1, 1))
                .withTom(LocalDate.of(2016, 2, 1))
                .withAktivitet(WSAktivitet()
                    .withAktivitetIkkeMulig(WSAktivitetIkkeMulig()
                        .withMedisinskeAarsaker(WSAarsak()
                            .withBeskrivelse("Årsaken er beskrevet")
                            .withAarsaker(Arrays.asList(
                                WSAarsaker()
                                    .withValue("Årsak 1"),
                                WSAarsaker()
                                    .withValue("Årsak 2")
                            ))))),
            WSPeriode()
                .withFom(LocalDate.of(2016, 3, 1))
                .withTom(LocalDate.of(2016, 4, 1))
                .withAktivitet(WSAktivitet()
                    .withGradertSykmelding(WSGradertSykmelding()
                        .withSykmeldingsgrad(50))
                ),
            WSPeriode()
                .withFom(LocalDate.of(2016, 5, 1))
                .withTom(LocalDate.of(2016, 6, 1))
                .withAktivitet(WSAktivitet()
                    .withAntallBehandlingsdagerUke(5)),
            WSPeriode()
                .withFom(LocalDate.of(2016, 7, 1))
                .withTom(LocalDate.of(2016, 8, 1))
                .withAktivitet(WSAktivitet()
                    .withHarReisetilskudd(true)),
            WSPeriode()
                .withFom(LocalDate.of(2016, 9, 1))
                .withTom(LocalDate.of(2016, 10, 1))
                .withAktivitet(WSAktivitet()
                    .withAvventendeSykmelding("Skikkelig avventende"))
        )
    }
}