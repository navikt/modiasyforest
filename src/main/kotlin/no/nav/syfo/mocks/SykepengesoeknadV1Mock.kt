package no.nav.syfo.mocks

import no.nav.syfo.config.consumer.SykepengesoknadConfig
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.*
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.informasjon.*
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ConditionalOnProperty(value = [SykepengesoknadConfig.MOCK_KEY], havingValue = "true")
class SykepengesoeknadV1Mock : SykepengesoeknadV1 {
    @Throws(HentSykepengesoeknadListeSikkerhetsbegrensning::class)
    override fun hentSykepengesoeknadListe(request: WSHentSykepengesoeknadListeRequest): WSHentSykepengesoeknadListeResponse {
        return WSHentSykepengesoeknadListeResponse()
            .withSykepengesoeknadListe(wsSykepengesoknadListe)
    }

    @Throws(HentSykepengesoeknadPdfSikkerhetsbegrensning::class)
    override fun hentSykepengesoeknadPdf(wsHentSykepengesoeknadPdfRequest: WSHentSykepengesoeknadPdfRequest): WSHentSykepengesoeknadPdfResponse? {
        return null
    }

    @Throws(BeregnArbeidsgiverperiodeSikkerhetsbegrensning::class)
    override fun beregnArbeidsgiverperiode(request: WSBeregnArbeidsgiverperiodeRequest): WSBeregnArbeidsgiverperiodeResponse? {
        return null
    }

    @Throws(HentNaermesteLedersSykepengesoeknadListeSikkerhetsbegrensning::class)
    override fun hentNaermesteLedersSykepengesoeknadListe(request: WSHentNaermesteLedersSykepengesoeknadListeRequest): WSHentNaermesteLedersSykepengesoeknadListeResponse? {
        return null
    }

    override fun ping() {}

    companion object {
        val wsSykepengesoknadListe = listOf(
            WSSykepengesoeknad()
                .withSykepengesoeknadId("sykepengesoeknadId")
                .withStatus("NY")
                .withHarBekreftetOpplysningsplikt(true)
                .withHarBekreftetKorrektInformasjon(true)
                .withSendtTilArbeidsgiverDato(LocalDate.now())
                .withSykmeldingSkrevetDato(LocalDate.now().minusDays(2))
                .withIdentdato(LocalDate.now().minusDays(5))
                .withArbeidGjenopptattDato(LocalDate.now().minusDays(2))
                .withOpprettetDato(LocalDate.now().minusDays(1))
                .withPeriode(WSPeriode()
                    .withFom(LocalDate.now().minusDays(2))
                    .withTom(LocalDate.now().plusDays(22))
                )
                .withFravaer(WSFravaer()
                    .withHarSoektOmSykepengerForOppholdet(false)
                    .withEgenmeldingsperiodeListe(
                        WSPeriode()
                            .withFom(LocalDate.now().minusDays(3))
                            .withTom(LocalDate.now()))
                    .withFerieListe(emptyList())
                    .withOppholdUtenforNorgeListe(
                        WSPeriode()
                            .withFom(LocalDate.now())
                            .withTom(LocalDate.now()))
                    .withPermisjonListe(emptyList()))
                .withSykmeldingsperiodeListe(listOf(
                    WSSykmeldingsperiode()
                        .withGraderingsperiode(WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().minusDays(1)))
                        .withSykmeldingsgrad(100)
                        .withKorrigertArbeidstid(WSKorrigertArbeidstid()
                            .withArbeidstimerNormaluke(37.5)
                            .withFaktiskeArbeidstimer(40.0)
                            .withBeregnetArbeidsgrad(19)
                        )))
                .withAnnenInntektskildeListe(
                    WSAnnenInntektskilde()
                        .withErSykmeldt(true)
                        .withType(WSAnnenInntektskildeType.FRILANSER))
                .withUtdanning(
                    WSUtdanning()
                        .withFom(LocalDate.now())
                        .withErFulltidsstudie(false))
                .withArbeidsgiverId("123456789")
                .withArbeidsgiverUtbetalerLoenn("NEI"),
            WSSykepengesoeknad()
                .withSykepengesoeknadId("sykepengesoeknadId2")
                .withStatus("AVBRUTT")
                .withAvbruttDato(LocalDate.now())
                .withHarBekreftetOpplysningsplikt(true)
                .withHarBekreftetKorrektInformasjon(true)
                .withSendtTilArbeidsgiverDato(LocalDate.now())
                .withSykmeldingSkrevetDato(LocalDate.now().minusDays(2))
                .withIdentdato(LocalDate.now().minusDays(5))
                .withArbeidGjenopptattDato(LocalDate.now().minusDays(2))
                .withOpprettetDato(LocalDate.now().minusDays(1))
                .withPeriode(WSPeriode()
                    .withFom(LocalDate.now().minusDays(2))
                    .withTom(LocalDate.now().plusDays(22))
                )
                .withFravaer(WSFravaer()
                    .withHarSoektOmSykepengerForOppholdet(false)
                    .withEgenmeldingsperiodeListe(
                        WSPeriode()
                            .withFom(LocalDate.now().minusDays(3))
                            .withTom(LocalDate.now()))
                    .withFerieListe(emptyList())
                    .withOppholdUtenforNorgeListe(
                        WSPeriode()
                            .withFom(LocalDate.now())
                            .withTom(LocalDate.now()))
                    .withPermisjonListe(emptyList()))
                .withSykmeldingsperiodeListe(listOf(
                    WSSykmeldingsperiode()
                        .withGraderingsperiode(WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().minusDays(1)))
                        .withSykmeldingsgrad(100)
                        .withKorrigertArbeidstid(WSKorrigertArbeidstid()
                            .withArbeidstimerNormaluke(37.5)
                            .withFaktiskeArbeidstimer(40.0)
                            .withBeregnetArbeidsgrad(29)
                        )))
                .withAnnenInntektskildeListe(
                    WSAnnenInntektskilde()
                        .withErSykmeldt(true)
                        .withType(WSAnnenInntektskildeType.FRILANSER))
                .withUtdanning(
                    WSUtdanning()
                        .withFom(LocalDate.now())
                        .withErFulltidsstudie(false))
                .withArbeidsgiverId("123456789")
                .withArbeidsgiverUtbetalerLoenn("NEI"),
            WSSykepengesoeknad()
                .withSykepengesoeknadId("sykepengesoeknadId3")
                .withStatus("SENDT")
                .withHarBekreftetOpplysningsplikt(true)
                .withHarBekreftetKorrektInformasjon(true)
                .withSendtTilArbeidsgiverDato(LocalDate.now())
                .withSendtTilNAVDato(LocalDate.now())
                .withSykmeldingSkrevetDato(LocalDate.now().minusDays(2))
                .withPeriode(WSPeriode()
                    .withFom(LocalDate.now().minusDays(2))
                    .withTom(LocalDate.now().plusDays(22))
                )
                .withIdentdato(LocalDate.now().minusDays(5))
                .withArbeidGjenopptattDato(LocalDate.now().minusDays(2))
                .withOpprettetDato(LocalDate.now().minusDays(1))
                .withFravaer(WSFravaer()
                    .withHarSoektOmSykepengerForOppholdet(false)
                    .withEgenmeldingsperiodeListe(
                        WSPeriode()
                            .withFom(LocalDate.now().minusDays(3))
                            .withTom(LocalDate.now()))
                    .withFerieListe(emptyList())
                    .withOppholdUtenforNorgeListe(
                        WSPeriode()
                            .withFom(LocalDate.now())
                            .withTom(LocalDate.now()))
                    .withPermisjonListe(emptyList()))
                .withSykmeldingsperiodeListe(listOf(
                    WSSykmeldingsperiode()
                        .withGraderingsperiode(WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().minusDays(1)))
                        .withSykmeldingsgrad(100)
                        .withKorrigertArbeidstid(WSKorrigertArbeidstid()
                            .withArbeidstimerNormaluke(37.5)
                            .withFaktiskeArbeidstimer(40.0)
                            .withBeregnetArbeidsgrad(39)
                        )))
                .withAnnenInntektskildeListe(
                    WSAnnenInntektskilde()
                        .withErSykmeldt(true)
                        .withType(WSAnnenInntektskildeType.FRILANSER))
                .withUtdanning(
                    WSUtdanning()
                        .withFom(LocalDate.now())
                        .withErFulltidsstudie(false))
                .withArbeidsgiverId("123456789")
                .withArbeidsgiverUtbetalerLoenn("NEI")
                .withOppsummering(null)
        )
    }
}
