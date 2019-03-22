package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.melding.virksomhet.sykepengesoeknadoppsummering.v1.sykepengesoeknadoppsummering.*;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad.*;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad.oppsummering.*;
import no.nav.sbl.dialogarena.modiasyforest.utils.JAXB;
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.informasjon.*;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykepengesoknad.oppsummering.Seksjon.*;
import static no.nav.sbl.dialogarena.modiasyforest.utils.MapUtil.*;

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


    private static Function<String, Ledetekst> xml2Ledetekst =
            tekst -> new Ledetekst().withTekst(tekst);

    private static Function<XMLTilleggstekst, Tilleggstekst> xml2Tilleggstekst = xmlTilleggstekst ->
            new Tilleggstekst()
                    .withLedetekst(map(xmlTilleggstekst.getTekst(), xml2Ledetekst))
                    .withType(Svartype.valueOf(xmlTilleggstekst.getType()));

    private static Function<XMLGruppe, Tilleggstekst> gruppe2Tilleggstekst = gruppe2Tilleggstekst ->
            mapMangetilEn(gruppe2Tilleggstekst.getTilleggstekst(), t -> true, xml2Tilleggstekst);

    private static Function<XMLSvar, Svar> xml2Svar = xmlSvar -> new Svar()
            .withLedetekst(map(xmlSvar.getTekst(), xml2Ledetekst))
            .withSvartype(Svartype.valueOf(xmlSvar.getType()))
            .withTilleggstekst(mapNullable(xmlSvar.getTilleggstekst(), xml2Tilleggstekst))
            .withUndersporsmal(mapListe(xmlSvar.getSporsmal(), xml2Sporsmal()));

    private static Function<XMLGruppe, Sporsmal> gruppe2Sporsmal = xmlGruppe ->
            mapMangetilEn(xmlGruppe.getSporsmal(), t -> true, xml2Sporsmal());

    private static Function<XMLSporsmal, Sporsmal> xml2Sporsmal() {
        return xmlSporsmal -> new Sporsmal()
                .withLedetekst(mapNullable(xmlSporsmal.getSporsmalstekst(), xml2Ledetekst))
                .withSvar(mapListe(xmlSporsmal.getSvar(), xml2Svar))
                .withType(mapNullable(xmlSporsmal.getType(), Sporsmalstype::valueOf));
    }

    private static Function<XMLGruppe, List<Sporsmal>> xml2Sporsmalsliste =
            xml -> xml
                    .getSporsmal()
                    .stream()
                    .map(xmlSporsmal -> map(xmlSporsmal, xml2Sporsmal()))
                    .collect(toList());

    private static final Function<XMLSykepengesoeknadOppsummering, Oppsummering> xml2Oppsummering = xmlOppsummering ->
            new Oppsummering()
                    .withSporsmalsliste(mapMangetilEn(xmlOppsummering.getGruppe(), g -> g.getId().equals(SOKNAD.name()), xml2Sporsmalsliste))
                    .withAnsvarserklaring(mapMangetilEn(xmlOppsummering.getGruppe(), g -> g.getId().equals(VAER_KLAR_OVER_AT.name()), gruppe2Tilleggstekst))
                    .withBekreftetKorrektInformasjon(mapMangetilEn(xmlOppsummering.getGruppe(), g -> g.getId().equals(BEKREFT_KORREKT_INFORMASJON.name()), gruppe2Sporsmal));

    public static final Function<WSSykepengesoeknad, Sykepengesoknad> ws2Sykepengesoknad =
            wsSykepengesoeknad ->
                    new Sykepengesoknad()
                            .withId(wsSykepengesoeknad.getSykepengesoeknadId())
                            .withSykmeldingId(wsSykepengesoeknad.getSykmeldingId())
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
                            .withFom(wsSykepengesoeknad.getPeriode().getFom())
                            .withTom(wsSykepengesoeknad.getPeriode().getTom())
                            .withDel(wsSykepengesoeknad.getDel())
                            .withAvbruttDato(wsSykepengesoeknad.getAvbruttDato())
                            .withForrigeSendteSoknadTom(wsSykepengesoeknad.getForrigeSykeforloepTom())
                            .withOppsummering(mapNullable(ofNullable(unmarshalXML(wsSykepengesoeknad)).map(o -> o.getValue()).orElse(null), xml2Oppsummering));

    public static <T, R> R mapMangetilEn(List<T> fra, Predicate<T> selector, Function<T, R> exp) {
        return ofNullable(fra).flatMap(g -> g.stream().filter(selector).map(exp).findFirst()).orElse(null);
    }

    private static JAXBElement<XMLSykepengesoeknadOppsummering> unmarshalXML(WSSykepengesoeknad wsSykepengesoeknad) {
        return (JAXBElement<XMLSykepengesoeknadOppsummering>) mapNullable(wsSykepengesoeknad.getOppsummering(), JAXB::unmarshalSykepengesoknadOppsummering);
    }
}
