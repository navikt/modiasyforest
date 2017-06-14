package no.nav.sbl.dialogarena.modiasyforest.mocks;


import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static java.util.Arrays.asList;

public class SykmeldingV1Mock implements SykmeldingV1 {

    @Override
    public WSHentNaermesteLedersSykmeldingListeResponse hentNaermesteLedersSykmeldingListe(WSHentNaermesteLedersSykmeldingListeRequest wsHentNaermesteLedersSykmeldingListeRequest) {
        return null;
    }

    public WSHentSykmeldingListeResponse hentSykmeldingListe(WSHentSykmeldingListeRequest request) {
        try {
            return new WSHentSykmeldingListeResponse()
                    .withMeldingListe(new WSMelding()
                            .withMeldingId("1")
                            .withArbeidsgiver("***REMOVED***")
                            .withIdentdato(now().minusDays(10))
                            .withSendtTilArbeidsgiverDato(LocalDateTime.now().minusDays(9))
                            .withStatus("SENDT")
                            .withSkalSkjermesForPasient(false)
                            .withSykmelding(new WSSykmelding()
                                    .withArbeidsgiver(new WSArbeidsgiver()
                                            .withNavn("NAV Consulting AS"))
                                    .withBehandler(new WSBehandler()
                                            .withNavn(new WSNavn()
                                                    .withFornavn("Lars")
                                                    .withEtternavn("Legesen"))
                                            .withKontaktinformasjon("22334455"))
                                    .withPasient(new WSPasient()
                                            .withFnr("01010199999")
                                            .withNavn(new WSNavn()
                                                    .withFornavn("Test")
                                                    .withEtternavn("Testesen")))
                                    .withMedisinskVurdering(new WSMedisinskVurdering()
                                            .withHoveddiagnose(new WSDiagnose()
                                                    .withValue("Skikkelig syk i kneet"))
                                            .withBidiagnoser(Arrays.asList(
                                                    new WSDiagnose()
                                                            .withValue("Bidiagnose 1")
                                                            .withKodeRef("kodeverk1"),
                                                    new WSDiagnose()
                                                            .withValue("Bidiagnose 2")
                                                            .withKodeRef("kodeverk2"),
                                                    new WSDiagnose()
                                                            .withValue("Bidiagnose 3")
                                                            .withKodeRef("kodeverk3")
                                            ))
                                            .withAnnenFravaersaarsak(new WSAarsak().withAarsaker(new WSAarsaker().withValue("Ingen (h)års(m)ak"))
                                                    .withBeskrivelse("Beskrivelse av fravær")))
                                    .withUtdypendeOpplysninger(new WSUtdypendeOpplysninger()
                                            .withSpoersmaal(new WSSpoersmaal().withSpoersmaalId("6.2.1")
                                                    .withSvar("jatakk, begge deler")))
                                    .withPrognose(new WSPrognose()
                                            .withErArbeidsfoerEtterEndtPeriode(true)
                                            .withArbeidsutsikter(new WSArbeidsutsikter().withErIArbeid(new WSErIArbeid()
                                                    .withHarEgetArbeidPaaSikt(true)
                                                    .withArbeidFom(now().plusDays(8)))))
                                    .withMeldingTilNav(new WSMeldingTilNav()
                                            .withTrengerBistandFraNavUmiddelbart(true))
                                    .withKontaktMedPasient(new WSKontaktMedPasient()
                                            .withBehandlet(LocalDateTime.now().minusDays(11)))
                                    .withSyketilfelleFom(now().minusDays(10))
                                    .withPerioder(perioderWS())
                            ));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public WSHentOppfoelgingstilfelleListeResponse hentOppfoelgingstilfelleListe(WSHentOppfoelgingstilfelleListeRequest wsHentOppfoelgingstilfelleListeRequest) {
        return new WSHentOppfoelgingstilfelleListeResponse()
                .withOppfoelgingstilfelleListe(asList(
                        new WSOppfoelgingstilfelle()
                                .withOppfoelgingsdato(now())
                ));
    }

    public void ping() {
    }


    private List<WSPeriode> perioderWS() throws Exception {
        return asList(
                new WSPeriode()
                        .withFom(now().minusDays(9))
                        .withTom(now().plusDays(11))
                        .withAktivitet(new WSAktivitet()
                                .withAktivitetIkkeMulig(new WSAktivitetIkkeMulig()
                                        .withMedisinskeAarsaker(new WSAarsak()
                                                .withBeskrivelse("Årsaken er beskrevet")
                                                .withAarsaker(asList(
                                                        new WSAarsaker()
                                                                .withValue("Årsak 1"),
                                                        new WSAarsaker()
                                                                .withValue("Årsak 2")
                                                ))))),
                new WSPeriode()
                        .withFom(now().plusDays(11))
                        .withTom(now().plusDays(12))
                        .withAktivitet(new WSAktivitet()
                                .withGradertSykmelding(new WSGradertSykmelding()
                                        .withSykmeldingsgrad(50))
                        ),
                new WSPeriode()
                        .withFom(now().plusDays(15))
                        .withTom(now().plusDays(19))
                        .withAktivitet(new WSAktivitet()
                                .withAntallBehandlingsdagerUke(5)),
                new WSPeriode()
                        .withFom(now().plusDays(22))
                        .withTom(now().plusDays(29))
                        .withAktivitet(new WSAktivitet()
                                .withHarReisetilskudd(true)),
                new WSPeriode()
                        .withFom(now().plusDays(29))
                        .withTom(now().plusDays(30))
                        .withAktivitet(new WSAktivitet()
                                .withAvventendeSykmelding("Skikkelig avventende"))

        );
    }

}
