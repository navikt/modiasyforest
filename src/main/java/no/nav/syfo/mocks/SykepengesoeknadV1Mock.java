package no.nav.syfo.mocks;

import no.nav.syfo.config.SykepengesoknadConfig;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.*;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.informasjon.WSAnnenInntektskildeType.FRILANSER;

@Service
@ConditionalOnProperty(value = SykepengesoknadConfig.MOCK_KEY, havingValue = "true")
public class SykepengesoeknadV1Mock implements SykepengesoeknadV1 {

    public static final List<WSSykepengesoeknad> wsSykepengesoknadListe = asList(
            new WSSykepengesoeknad()
                    .withSykepengesoeknadId("sykepengesoeknadId")
                    .withStatus("NY")
                    .withHarBekreftetOpplysningsplikt(true)
                    .withHarBekreftetKorrektInformasjon(true)
                    .withSendtTilArbeidsgiverDato(now())
                    .withSykmeldingSkrevetDato(now().minusDays(2))
                    .withIdentdato(now().minusDays(5))
                    .withArbeidGjenopptattDato(now().minusDays(2))
                    .withOpprettetDato(now().minusDays(1))
                    .withPeriode(new WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().plusDays(22))
                    )
                    .withFravaer(new WSFravaer()
                            .withHarSoektOmSykepengerForOppholdet(false)
                            .withEgenmeldingsperiodeListe(
                                    new WSPeriode()
                                            .withFom(now().minusDays(3))
                                            .withTom(now()))
                            .withFerieListe(emptyList())
                            .withOppholdUtenforNorgeListe(
                                    new WSPeriode()
                                            .withFom(now())
                                            .withTom(now()))
                            .withPermisjonListe(emptyList()))
                    .withSykmeldingsperiodeListe(asList(
                            new WSSykmeldingsperiode()
                                    .withGraderingsperiode(new WSPeriode()
                                            .withFom(now().minusDays(2))
                                            .withTom(now().minusDays(1)))
                                    .withSykmeldingsgrad(100)
                                    .withKorrigertArbeidstid(new WSKorrigertArbeidstid()
                                            .withArbeidstimerNormaluke(37.5)
                                            .withFaktiskeArbeidstimer(40.0)
                                            .withBeregnetArbeidsgrad(19)
                                    )))
                    .withAnnenInntektskildeListe(
                            new WSAnnenInntektskilde()
                                    .withErSykmeldt(true)
                                    .withType(FRILANSER))
                    .withUtdanning(
                            new WSUtdanning()
                                    .withFom(now())
                                    .withErFulltidsstudie(false))
                    .withArbeidsgiverId("123456789")
                    .withArbeidsgiverUtbetalerLoenn("NEI"),
            new WSSykepengesoeknad()
                    .withSykepengesoeknadId("sykepengesoeknadId2")
                    .withStatus("AVBRUTT")
                    .withAvbruttDato(LocalDate.now())
                    .withHarBekreftetOpplysningsplikt(true)
                    .withHarBekreftetKorrektInformasjon(true)
                    .withSendtTilArbeidsgiverDato(now())
                    .withSykmeldingSkrevetDato(now().minusDays(2))
                    .withIdentdato(now().minusDays(5))
                    .withArbeidGjenopptattDato(now().minusDays(2))
                    .withOpprettetDato(now().minusDays(1))
                    .withPeriode(new WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().plusDays(22))
                    )
                    .withFravaer(new WSFravaer()
                            .withHarSoektOmSykepengerForOppholdet(false)
                            .withEgenmeldingsperiodeListe(
                                    new WSPeriode()
                                            .withFom(now().minusDays(3))
                                            .withTom(now()))
                            .withFerieListe(emptyList())
                            .withOppholdUtenforNorgeListe(
                                    new WSPeriode()
                                            .withFom(now())
                                            .withTom(now()))
                            .withPermisjonListe(emptyList()))
                    .withSykmeldingsperiodeListe(asList(
                            new WSSykmeldingsperiode()
                                    .withGraderingsperiode(new WSPeriode()
                                            .withFom(now().minusDays(2))
                                            .withTom(now().minusDays(1)))
                                    .withSykmeldingsgrad(100)
                                    .withKorrigertArbeidstid(new WSKorrigertArbeidstid()
                                            .withArbeidstimerNormaluke(37.5)
                                            .withFaktiskeArbeidstimer(40.0)
                                            .withBeregnetArbeidsgrad(29)
                                    )))
                    .withAnnenInntektskildeListe(
                            new WSAnnenInntektskilde()
                                    .withErSykmeldt(true)
                                    .withType(FRILANSER))
                    .withUtdanning(
                            new WSUtdanning()
                                    .withFom(now())
                                    .withErFulltidsstudie(false))
                    .withArbeidsgiverId("123456789")
                    .withArbeidsgiverUtbetalerLoenn("NEI"),
            new WSSykepengesoeknad()
                    .withSykepengesoeknadId("sykepengesoeknadId3")
                    .withStatus("SENDT")
                    .withHarBekreftetOpplysningsplikt(true)
                    .withHarBekreftetKorrektInformasjon(true)
                    .withSendtTilArbeidsgiverDato(now())
                    .withSendtTilNAVDato(now())
                    .withSykmeldingSkrevetDato(now().minusDays(2))
                    .withPeriode(new WSPeriode()
                            .withFom(LocalDate.now().minusDays(2))
                            .withTom(LocalDate.now().plusDays(22))
                    )
                    .withIdentdato(now().minusDays(5))
                    .withArbeidGjenopptattDato(now().minusDays(2))
                    .withOpprettetDato(now().minusDays(1))
                    .withFravaer(new WSFravaer()
                            .withHarSoektOmSykepengerForOppholdet(false)
                            .withEgenmeldingsperiodeListe(
                                    new WSPeriode()
                                            .withFom(now().minusDays(3))
                                            .withTom(now()))
                            .withFerieListe(emptyList())
                            .withOppholdUtenforNorgeListe(
                                    new WSPeriode()
                                            .withFom(now())
                                            .withTom(now()))
                            .withPermisjonListe(emptyList()))
                    .withSykmeldingsperiodeListe(asList(
                            new WSSykmeldingsperiode()
                                    .withGraderingsperiode(new WSPeriode()
                                            .withFom(now().minusDays(2))
                                            .withTom(now().minusDays(1)))
                                    .withSykmeldingsgrad(100)
                                    .withKorrigertArbeidstid(new WSKorrigertArbeidstid()
                                            .withArbeidstimerNormaluke(37.5)
                                            .withFaktiskeArbeidstimer(40.0)
                                            .withBeregnetArbeidsgrad(39)
                                    )))
                    .withAnnenInntektskildeListe(
                            new WSAnnenInntektskilde()
                                    .withErSykmeldt(true)
                                    .withType(FRILANSER))
                    .withUtdanning(
                            new WSUtdanning()
                                    .withFom(now())
                                    .withErFulltidsstudie(false))
                    .withArbeidsgiverId("123456789")
                    .withArbeidsgiverUtbetalerLoenn("NEI")
                    .withOppsummering(null)
    );

    @Override
    public WSHentSykepengesoeknadListeResponse hentSykepengesoeknadListe(WSHentSykepengesoeknadListeRequest request) throws HentSykepengesoeknadListeSikkerhetsbegrensning {
        return new WSHentSykepengesoeknadListeResponse()
                .withSykepengesoeknadListe(wsSykepengesoknadListe);
    }

    @Override
    public WSHentSykepengesoeknadPdfResponse hentSykepengesoeknadPdf(WSHentSykepengesoeknadPdfRequest wsHentSykepengesoeknadPdfRequest) throws HentSykepengesoeknadPdfSikkerhetsbegrensning {
        return null;
    }

    @Override
    public WSBeregnArbeidsgiverperiodeResponse beregnArbeidsgiverperiode(WSBeregnArbeidsgiverperiodeRequest request) throws BeregnArbeidsgiverperiodeSikkerhetsbegrensning {
        return null;
    }

    @Override
    public WSHentNaermesteLedersSykepengesoeknadListeResponse hentNaermesteLedersSykepengesoeknadListe(WSHentNaermesteLedersSykepengesoeknadListeRequest request) throws HentNaermesteLedersSykepengesoeknadListeSikkerhetsbegrensning {
        return null;
    }

    @Override
    public void ping() {
    }
}
