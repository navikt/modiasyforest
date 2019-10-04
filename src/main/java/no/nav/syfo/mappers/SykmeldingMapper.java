package no.nav.syfo.mappers;

import no.nav.syfo.controller.domain.sykepengesoknad.Datospenn;
import no.nav.syfo.controller.domain.sykmelding.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.syfo.utils.MapUtil.mapListe;
import static no.nav.syfo.utils.MapUtil.mapNullable;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@SuppressWarnings({"squid:S1200", "squid:S1188"})
public class SykmeldingMapper {

    public static List<Sykmelding> sykmeldinger(final List<WSMelding> sykmeldingerWS) {
        return sykmeldingerWS.stream()
                .map(wsMelding -> sykmelding(wsMelding))
                .collect(toList());
    }

    private static Function<WSDatospenn, Datospenn> ws2Datospenn = wsDatospenn ->
            new Datospenn()
                    .withFom(wsDatospenn.getFom())
                    .withTom(wsDatospenn.getTom());

    private static Function<WSSMSpoersmaal, Skjemasporsmal> ws2Sporsmal = wssmSpoersmaal ->
            new Skjemasporsmal()
                    .withArbeidssituasjon(wssmSpoersmaal.getArbeidssituasjon().value())
                    .withHarForsikring(wssmSpoersmaal.isHarForsikringsgrad())
                    .withFravaersperioder(mapListe(wssmSpoersmaal.getAnnenFravaersperiodeListe(), ws2Datospenn))
                    .withHarAnnetFravaer(wssmSpoersmaal.isHarAnnetFravaer());

    public static Sykmelding sykmelding(final WSMelding sykmeldingerWS) {
        Function<WSMelding, Sykmelding> toSykmelding =
                m -> {
                    WSSykmelding sm = m.getSykmelding();
                    Sykmelding sykmelding = new Sykmelding()
                            .withId(id(m))
                            .withFnr(fnr(sm))
                            .withFornavn(fornavn(sm))
                            .withMellomnavn(mellomnavn(sm))
                            .withEtternavn(etternavn(sm))
                            .withSykmelder(sykmelder(sm))
                            .withMottakendeArbeidsgiver(mottakendeArbeidsgiver(m.getMottakendeArbeidsgiver()))
                            .withOrgnummer(mapNullable(m.getMottakendeArbeidsgiver(), WSMottakendeArbeidsgiver::getVirksomhetsnummer, m.getArbeidsgiver()))
                            .withStatus(m.getStatus())
                            .withSendtTilArbeidsgiverDato(sykmeldingerWS.getSendtTilArbeidsgiverDato())
                            .withHoveddiagnose(hoveddiagnose(sm))
                            .withBidiagnose(bidiagnoser(sm))
                            .withFravaersgrunnLovfestet(fravaersgrunn(sm))
                            .withFravaerBeskrivelse(fravaerBeskrivelse(sm))
                            .withSykmelderTlf(sykmelderTlf(sm))
                            .withArbeidsgiver(arbeidsgiver(sm))
                            .withStillingsprosent(stillingsprosent(sm))
                            .withValgtArbeidssituasjon(arbeidssituasjon(m))
                            .withArbeidsfoerEtterPerioden(arbeidsfoer(sm))
                            .withStartLegemeldtFravaer(fravaerStart(sm))
                            .withAntarReturSammeArbeidsgiver(returTilSammeArbeidsgiver(sm))
                            .withAntattDatoReturSammeArbeidsgiver(returTilSammeArbeidsgiverDato(sm))
                            .withAntarReturAnnenArbeidsgiver(returTilAnnenArbeidsgiver(sm))
                            .withTilbakemeldingReturArbeid(tilbakemeldingRetur(sm))
                            .withNavBoerTaTakISaken(navTaTakISaken(sm))
                            .withSvangerskap(erSvangerskapsrelatert(sm))
                            .withYrkesskade(skyldesYrkesskade(sm))
                            .withYrkesskadeDato(sm.getMedisinskVurdering().getYrkesskadeDato())
                            .withHensynPaaArbeidsplassen(hensynPaaArbeidsplassen(sm))
                            .withUtenArbeidsgiverAntarTilbakeIArbeid(erArbeidsfoerPaaSikt(sm))
                            .withUtenArbeidsgiverAntarTilbakeIArbeidDato(arbeidsfoerFom(sm))
                            .withUtenArbeidsgiverTilbakemelding(utenArbeidsgiverTilbakemelding(sm))
                            .withTilretteleggingArbeidsplass(tiltakArbeidsplassen(sm))
                            .withTiltakNAV(tiltakNav(sm))
                            .withTiltakAndre(andreTiltak(sm))
                            .withNavBoerTaTakISakenBegrunnelse(navBoerTaTakISakenBegrunnelse(sm))
                            .withInnspillTilArbeidsgiver(meldingTilArbeidsgiver(sm))
                            .withDokumenterbarPasientkontakt(dokumenterbarPasientkontakt(sm))
                            .withTilbakedatertBegrunnelse(begrunnIkkeKontakt(sm))
                            .withUtstedelsesdato(utstedelsesdato(sm))
                            .withSkalViseSkravertFelt(skalViseSkravertFelt(m))
                            .withIdentdato(identdato(m))
                            .withSporsmal(mapNullable(m.getSmSpoersmaal(), ws2Sporsmal));

                    perioder(sm, sykmelding);
                    utdypendeOpplysninger(sm, sykmelding);

                    return sykmelding;
                };
        return toSykmelding.apply(sykmeldingerWS);
    }

    private static LocalDate identdato(WSMelding m) {
        /* Fallbacker til fravaersstart om vi ikke ha identdato */
        return m.getIdentdato() != null ? m.getIdentdato() : fravaerStart(m.getSykmelding());
    }

    private static Boolean skalViseSkravertFelt(WSMelding m) {
        return m.isSkalSkjermesForPasient() == null || !m.isSkalSkjermesForPasient();
    }

    private static String begrunnIkkeKontakt(WSSykmelding sm) {
        return ofNullable(sm.getKontaktMedPasient())
                .map(WSKontaktMedPasient::getBegrunnIkkeKontakt)
                .orElse(null);
    }

    private static LocalDate dokumenterbarPasientkontakt(WSSykmelding sm) {
        return ofNullable(sm.getKontaktMedPasient())
                .flatMap(kontaktMedPasient -> ofNullable(kontaktMedPasient.getKontakt()))
                .orElse(null);
    }

    private static String meldingTilArbeidsgiver(WSSykmelding sm) {
        return sm.getMeldingTilArbeidsgiver();
    }

    private static String navBoerTaTakISakenBegrunnelse(WSSykmelding sm) {
        return ofNullable(sm.getMeldingTilNav())
                .map(WSMeldingTilNav::getBeskrivelseBistandNav)
                .orElse(null);

    }

    private static String andreTiltak(WSSykmelding sm) {
        return ofNullable(sm.getTiltak())
                .map(WSTiltak::getAndreTiltak)
                .orElse(null);
    }

    private static String tiltakNav(WSSykmelding sm) {
        return ofNullable(sm.getTiltak())
                .map(WSTiltak::getTiltakNav)
                .orElse(null);
    }

    private static Boolean erSvangerskapsrelatert(WSSykmelding sm) {
        return sm.getMedisinskVurdering().isErSvangerskapsrelatert();
    }

    private static Boolean skyldesYrkesskade(WSSykmelding sm) {
        return sm.getMedisinskVurdering().isErYrkesskade();
    }

    private static String tiltakArbeidsplassen(WSSykmelding sm) {
        return ofNullable(sm.getTiltak())
                .map(WSTiltak::getTiltakArbeidsplassen)
                .orElse(null);
    }

    private static LocalDate utenArbeidsgiverTilbakemelding(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIkkeIArbeid()))
                .flatMap(erIkkeIArbeid -> ofNullable(erIkkeIArbeid.getVurderingsdato()))
                .orElse(null);
    }

    private static LocalDate arbeidsfoerFom(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIkkeIArbeid()))
                .flatMap(erIkkeIArbeid -> ofNullable(erIkkeIArbeid.getArbeidsfoerFom()))
                .orElse(null);
    }

    private static boolean erArbeidsfoerPaaSikt(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIkkeIArbeid()))
                .flatMap(erIkkeIArbeid -> ofNullable(erIkkeIArbeid.isErArbeidsfoerPaaSikt()))
                .orElse(false);
    }

    private static String hensynPaaArbeidsplassen(WSSykmelding sm) {
        return sm.getPrognose() != null ? sm.getPrognose().getBeskrivHensynArbeidsplassen() : null;
    }

    private static String id(WSMelding s) {
        return s.getMeldingId();
    }

    private static String fnr(WSSykmelding s) {
        return s.getPasient().getFnr();
    }

    private static String fornavn(WSSykmelding s) {
        return s.getPasient().getNavn().getFornavn();
    }

    private static String mellomnavn(WSSykmelding s) {
        return Optional.ofNullable(s.getPasient().getNavn().getMellomnavn()).orElse("");
    }

    private static String etternavn(WSSykmelding s) {
        return s.getPasient().getNavn().getEtternavn();
    }

    private static String sykmelder(WSSykmelding s) {
        String mellomnavn = isNotBlank(s.getBehandler().getNavn().getMellomnavn()) ? " " + s.getBehandler().getNavn().getMellomnavn() + " " : " ";
        return s.getBehandler().getNavn().getFornavn() + mellomnavn + s.getBehandler().getNavn().getEtternavn();
    }

    private static Diagnose hoveddiagnose(WSSykmelding s) {
        if (isTrue(s.getMedisinskVurdering().isSkalSkjermesForPasient())) {
            return null;
        }

        WSDiagnose d = s.getMedisinskVurdering().getHoveddiagnose();
        if (d != null) {
            Diagnose diagnose = new Diagnose();
            diagnose.diagnose = d.getValue();
            diagnose.diagnosekode = d.getKodeRef();
            diagnose.diagnosesystem = d.getKodeverksRef();
            return diagnose;
        }
        return null;
    }

    private static List<Diagnose> bidiagnoser(WSSykmelding s) {
        if (isTrue(s.getMedisinskVurdering().isSkalSkjermesForPasient())) {
            return emptyList();
        }

        List<Diagnose> bidiagnoser = new ArrayList<>();
        for (WSDiagnose b : s.getMedisinskVurdering().getBidiagnoser()) {
            Diagnose bidiagnose = new Diagnose();
            bidiagnose.diagnose = b.getValue();
            bidiagnose.diagnosekode = b.getKodeRef();
            bidiagnose.diagnosesystem = b.getKodeverksRef();
            bidiagnoser.add(bidiagnose);
        }

        return bidiagnoser;
    }

    private static String fravaersgrunn(WSSykmelding s) {
        if (isTrue(s.getMedisinskVurdering().isSkalSkjermesForPasient())) {
            return null;
        }

        WSAarsak fravaerAarsak = s.getMedisinskVurdering().getAnnenFravaersaarsak();
        if (fravaerAarsak != null && !fravaerAarsak.getAarsaker().isEmpty()) {
            return fravaerAarsak.getAarsaker().get(0).getValue();
        }
        return null;
    }

    private static String fravaerBeskrivelse(WSSykmelding s) {
        if (isTrue(s.getMedisinskVurdering().isSkalSkjermesForPasient())) {
            return null;
        }

        WSAarsak fravaerAarsak = s.getMedisinskVurdering().getAnnenFravaersaarsak();
        if (fravaerAarsak != null) {
            return fravaerAarsak.getBeskrivelse();
        }
        return null;
    }

    private static void perioder(WSSykmelding s, Sykmelding sykmelding) {
        List<Periode> perioder = new ArrayList<>();
        for (WSPeriode p : s.getPerioder()) {
            Periode periode = new Periode();
            periode.fom = p.getFom();
            periode.tom = p.getTom();

            if (p.getAktivitet() != null) {
                WSAktivitetIkkeMulig aktivitetIkkeMulig = p.getAktivitet().getAktivitetIkkeMulig();
                if (aktivitetIkkeMulig != null) {
                    periode.grad = 100;

                    sykmelding.mulighetForArbeid.aktivitetIkkeMulig433 = ofNullable(aktivitetIkkeMulig.getMedisinskeAarsaker())
                            .map(WSAarsak::getAarsaker)
                            .map(aarsakListe -> aarsakListe.stream().map(WSKodeverdi::getValue))
                            .orElse(Stream.empty())
                            .collect(toList());

                    sykmelding.mulighetForArbeid.aktivitetIkkeMulig434 = ofNullable(aktivitetIkkeMulig.getMuligTilretteleggingPaaArbeidsplassen())
                            .map(WSAarsak::getAarsaker)
                            .map(aarsakListe -> aarsakListe.stream().map(WSKodeverdi::getValue))
                            .orElse(Stream.empty())
                            .collect(toList());

                    sykmelding.mulighetForArbeid.aarsakAktivitetIkkeMulig433 = ofNullable(aktivitetIkkeMulig.getMedisinskeAarsaker())
                            .map(WSAarsak::getBeskrivelse)
                            .orElse(null);
                    sykmelding.mulighetForArbeid.aarsakAktivitetIkkeMulig434 = ofNullable(aktivitetIkkeMulig.getMuligTilretteleggingPaaArbeidsplassen())
                            .map(WSAarsak::getBeskrivelse)
                            .orElse(null);
                }

                WSGradertSykmelding gradertSykmelding = p.getAktivitet().getGradertSykmelding();
                if (gradertSykmelding != null) {
                    periode.grad = gradertSykmelding.getSykmeldingsgrad();
                    periode.reisetilskudd = gradertSykmelding.isHarReisetilskudd();
                }

                if (p.getAktivitet().getAntallBehandlingsdagerUke() != null) {
                    periode.behandlingsdager = p.getAktivitet().getAntallBehandlingsdagerUke();
                }

                if (isTrue(p.getAktivitet().isHarReisetilskudd())) {
                    periode.reisetilskudd = true;
                }

                if (isNotBlank(p.getAktivitet().getAvventendeSykmelding())) {
                    periode.avventende = p.getAktivitet().getAvventendeSykmelding();
                }
            }

            perioder.add(periode);
        }
        sykmelding.mulighetForArbeid.perioder = perioder;
    }

    private static String sykmelderTlf(WSSykmelding s) {
        return s.getBehandler().getKontaktinformasjon().get(0);
    }

    private static String arbeidsgiver(WSSykmelding s) {
        return s.getArbeidsgiver() != null ? s.getArbeidsgiver().getNavn() : null;
    }

    private static Integer stillingsprosent(final WSSykmelding wsSykmelding) {
        return wsSykmelding.getArbeidsgiver() != null ? wsSykmelding.getArbeidsgiver().getStillingsprosent() : null;
    }

    private static MottakendeArbeidsgiver mottakendeArbeidsgiver(final WSMottakendeArbeidsgiver wsMottakendeArbeidsgiver) {
        return mapNullable(wsMottakendeArbeidsgiver, ma -> new MottakendeArbeidsgiver()
                .withNavn(ma.getNavn())
                .withVirksomhetsnummer(ma.getVirksomhetsnummer())
                .withJuridiskOrgnummer(ma.getJuridiskOrgnummer()));
    }

    private static String arbeidssituasjon(WSMelding m) {
        return m.getArbeidssituasjon() != null ? m.getArbeidssituasjon().name() : null;
    }

    private static Boolean arbeidsfoer(WSSykmelding s) {
        return s.getPrognose() != null ? s.getPrognose().isErArbeidsfoerEtterEndtPeriode() : null;
    }

    private static LocalDate fravaerStart(WSSykmelding s) {
        return s.getSyketilfelleFom();
    }

    private static boolean navTaTakISaken(WSSykmelding sm) {
        return sm.getMeldingTilNav() != null && sm.getMeldingTilNav().isTrengerBistandFraNavUmiddelbart();
    }

    private static void utdypendeOpplysninger(WSSykmelding sm, Sykmelding sykmelding) {
        boolean harUtdypendeOpplysninger = !sm.getUtdypendeOpplysninger().isEmpty();

        if (harUtdypendeOpplysninger) {
            List<WSSpoersmaal> spoersmaals = sm.getUtdypendeOpplysninger().get(0).getSpoersmaal();

            for (WSSpoersmaal spoersmaal : spoersmaals) {
                switch (spoersmaal.getSpoersmaalId()) {
                    case "6.2.1":
                        sykmelding.utdypendeOpplysninger.sykehistorie = spoersmaal.getSvar();
                        break;
                    case "6.2.2":
                        sykmelding.utdypendeOpplysninger.paavirkningArbeidsevne = spoersmaal.getSvar();
                        break;
                    case "6.2.3":
                        sykmelding.utdypendeOpplysninger.resultatAvBehandling = spoersmaal.getSvar();
                        break;
                    case "6.2.4":
                        sykmelding.utdypendeOpplysninger.henvisningUtredningBehandling = spoersmaal.getSvar();
                        break;
                    case "6.3.1":
                        sykmelding.utdypendeOpplysninger.sykehistoriePunkt63 = spoersmaal.getSvar();
                        break;
                    case "6.3.2":
                        sykmelding.utdypendeOpplysninger.henvisningUtredningBehandlingPunkt63 = spoersmaal.getSvar();
                        break;
                }
            }
        }
    }

    private static Boolean returTilSammeArbeidsgiver(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIArbeid()))
                .flatMap(erIArbeid -> ofNullable(erIArbeid.isHarEgetArbeidPaaSikt()))
                .orElse(false);
    }

    private static LocalDate returTilSammeArbeidsgiverDato(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIArbeid()))
                .flatMap(erIArbeid -> ofNullable(erIArbeid.getArbeidFom()))
                .orElse(null);
    }

    private static boolean returTilAnnenArbeidsgiver(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIArbeid()))
                .flatMap(erIArbeid -> ofNullable(erIArbeid.isHarAnnetArbeidPaaSikt()))
                .orElse(false);
    }

    private static LocalDate tilbakemeldingRetur(WSSykmelding sm) {
        return ofNullable(sm.getPrognose())
                .flatMap(prognose -> ofNullable(prognose.getArbeidsutsikter()))
                .flatMap(arbeidsutsikter -> ofNullable(arbeidsutsikter.getErIArbeid()))
                .flatMap(erIArbeid -> ofNullable(erIArbeid.getVurderingsdato()))
                .orElse(null);
    }

    private static LocalDate utstedelsesdato(WSSykmelding sm) {
        if (sm.getKontaktMedPasient() == null) {
            return null;
        }
        return sm.getKontaktMedPasient().getBehandlet().toLocalDate();
    }
}
