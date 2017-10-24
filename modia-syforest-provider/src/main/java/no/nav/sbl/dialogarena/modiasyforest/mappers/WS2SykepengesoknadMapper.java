package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad.*;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.informasjon.*;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static no.nav.sbl.java8utils.MapUtil.map;
import static no.nav.sbl.java8utils.MapUtil.mapListe;
import static no.nav.sbl.java8utils.MapUtil.mapNullable;

public abstract class WS2SykepengesoknadMapper {

    private static final Function<WSKorrigertArbeidstid, Avvik> ws2Avvik = wsKorrigertArbeidstid ->
            new Avvik()
                    .withArbeidsgrad(wsKorrigertArbeidstid.getArbeidsgrad())
                    .withArbeidstimerNormalUke(wsKorrigertArbeidstid.getArbeidstimerNormaluke())
                    .withTimer(wsKorrigertArbeidstid.getFaktiskeArbeidstimer())
                    .withBeregnetArbeidsgrad(wsKorrigertArbeidstid.getBeregnetArbeidsgrad());

    private static final Function<WSAnnenInntektskilde, AnnenInntektskilde> ws2AndreInntektskilder =
            wsAndreInntektskilder -> new AnnenInntektskilde()
                    .withAnnenInntektskildeType(AnnenInntektskildeType.valueOf(wsAndreInntektskilder.getType().value()))
                    .withSykmeldt(wsAndreInntektskilder.isErSykmeldt());

    public static final Function<WSPeriode, Datospenn> ws2Datospenn =
            wsPeriode -> new Datospenn()
                    .withFom(wsPeriode.getFom())
                    .withTom(wsPeriode.getTom());

    private static final Function<WSSykmeldingsperiode, Aktivitet> ws2Aktivitet =
            wsAktivitet -> new Aktivitet()
                    .withAvvik(mapNullable(wsAktivitet.getKorrigertArbeidstid(), ws2Avvik))
                    .withDatospenn(map(wsAktivitet.getGraderingsperiode(), ws2Datospenn))
                    .withGrad(wsAktivitet.getSykmeldingsgrad());

    private static final Function<WSFravaer, Utenlandsopphold> ws2Utenlandsopphold =
            wsFravaer -> {
                if (wsFravaer.getOppholdUtenforNorgeListe().isEmpty()) {
                    return null;
                } else {
                    return new Utenlandsopphold()
                            .withSoektOmSykepengerIPerioden(wsFravaer.isHarSoektOmSykepengerForOppholdet())
                            .withDatospenn(mapListe(wsFravaer.getOppholdUtenforNorgeListe(), ws2Datospenn));
                }
            };

    private static final Function<WSUtdanning, Utdanning> ws2Utdanning =
            wsUtdanning -> new Utdanning()
                    .withErUtdanningFulltidsstudium(wsUtdanning.isErFulltidsstudie())
                    .withUtdanningStartdato(wsUtdanning.getFom());

    private static final Function<WSFravaer, List<Datospenn>> ws2Egenmeldingsperiode =
            wsFravaer -> mapListe(wsFravaer.getEgenmeldingsperiodeListe(), ws2Datospenn);

    private static final Function<WSFravaer, List<Datospenn>> ws2Permisjon =
            wsFravaer -> mapListe(wsFravaer.getPermisjonListe(), ws2Datospenn);

    private static final Function<WSFravaer, List<Datospenn>> ws2Ferie =
            wsFravaer -> mapListe(wsFravaer.getFerieListe(), ws2Datospenn);

    public static final Function<WSSykepengesoeknad, Sykepengesoknad> ws2Sykepengesoknad =
            wsSykepengesoeknad ->
                    new Sykepengesoknad()
                            .withId(wsSykepengesoeknad.getSykepengesoeknadId())
                            .withStatus(wsSykepengesoeknad.getStatus())
                            .withOpprettetDato(wsSykepengesoeknad.getOpprettetDato())
                            .withAnsvarBekreftet(wsSykepengesoeknad.isHarBekreftetOpplysningsplikt())
                            .withBekreftetKorrektInformasjon(wsSykepengesoeknad.isHarBekreftetKorrektInformasjon())
                            .withIdentdato(wsSykepengesoeknad.getIdentdato())
                            .withGjenopptattArbeidFulltUtDato(wsSykepengesoeknad.getArbeidGjenopptattDato())
                            .withEgenmeldingsperioder(ofNullable(wsSykepengesoeknad.getFravaer()).map(f -> map(f, ws2Egenmeldingsperiode)).orElse(emptyList()))
                            .withFerie(ofNullable(wsSykepengesoeknad.getFravaer()).map(f -> map(f, ws2Ferie)).orElse(emptyList()))
                            .withPermisjon(ofNullable(wsSykepengesoeknad.getFravaer()).map(f -> map(f, ws2Permisjon)).orElse(emptyList()))
                            .withUtenlandsopphold(ofNullable(wsSykepengesoeknad.getFravaer()).map(f -> mapNullable(f, ws2Utenlandsopphold)).orElse(null))
                            .withAktiviteter(mapListe(wsSykepengesoeknad.getSykmeldingsperiodeListe(), ws2Aktivitet))
                            .withAndreInntektskilder(mapListe(wsSykepengesoeknad.getAnnenInntektskildeListe(), ws2AndreInntektskilder))
                            .withUtdanning(mapNullable(wsSykepengesoeknad.getUtdanning(), ws2Utdanning))
                            .withArbeidsgiverForskutterer("IKKE_SPURT".equals(wsSykepengesoeknad.getArbeidsgiverUtbetalerLoenn()) ? null : wsSykepengesoeknad.getArbeidsgiverUtbetalerLoenn())
                            .withArbeidsgiver(new Arbeidsgiver().withOrgnummer(wsSykepengesoeknad.getArbeidsgiverId()))
                            .withSykmeldingSkrevetDato(wsSykepengesoeknad.getSykmeldingSkrevetDato())
                            .withSendtTilNAVDato(wsSykepengesoeknad.getSendtTilNAVDato())
                            .withSendtTilArbeidsgiverDato(wsSykepengesoeknad.getSendtTilArbeidsgiverDato())
                            .withForrigeSykeforloepTom(wsSykepengesoeknad.getForrigeSykeforloepTom())
                            .withKorrigerer(wsSykepengesoeknad.getKorrigerer())
                          //  .withFom(wsSykepengesoeknad.getPeriode().getFom())
                           // .withTom(wsSykepengesoeknad.getPeriode().getTom())
                            .withDel(wsSykepengesoeknad.getDel());
}