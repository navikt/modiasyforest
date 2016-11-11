package no.nav.sbl.dialogarena.modiasyforest.mocks;


import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSStatus.SENDT;

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
                            .withArbeidsgiver("123456789")
                            .withIdentdato(of(2016, 2, 1))
                            .withSendtTilArbeidsgiverDato(LocalDateTime.of(2016, 6, 6, 12, 40))
                            .withStatus(SENDT)
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
                                            .withAnnenFravaersaarsak(new WSAarsak().withAarsaker(new WSAarsaker().withValue("Ingen (h)års(m)ak"))
                                                    .withBeskrivelse("Beskrivelse av fravær")))
                                    .withUtdypendeOpplysninger(new WSUtdypendeOpplysninger()
                                            .withSpoersmaal(new WSSpoersmaal().withSpoersmaalId("6.2.1")
                                                    .withSvar("jatakk, begge deler")))
                                    .withPrognose(new WSPrognose()
                                            .withErArbeidsfoerEtterEndtPeriode(true)
                                            .withArbeidsutsikter(new WSArbeidsutsikter().withErIArbeid(new WSErIArbeid()
                                                    .withHarEgetArbeidPaaSikt(true)
                                                    .withArbeidFom(of(2016, 4, 4)))))
                                    .withMeldingTilNav(new WSMeldingTilNav()
                                            .withTrengerBistandFraNavUmiddelbart(true))
                                    .withKontaktMedPasient(new WSKontaktMedPasient()
                                            .withBehandlet(LocalDateTime.of(2016, 1, 1, 12, 12)))
                                    .withSyketilfelleFom(of(2016, 2, 1))
                                    .withPerioder(perioderWS())
                            ));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public WSHentOppfoelgingstilfelleListeResponse hentOppfoelgingstilfelleListe(WSHentOppfoelgingstilfelleListeRequest wsHentOppfoelgingstilfelleListeRequest) {
        return null;
    }

    public void ping() {
    }


    private List<WSPeriode> perioderWS() throws Exception {
        return asList(
                new WSPeriode()
                        .withFom(of(2016, 1, 1))
                        .withTom(of(2016, 2, 1))
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
                        .withFom(of(2016, 3, 1))
                        .withTom(of(2016, 4, 1))
                        .withAktivitet(new WSAktivitet()
                                .withGradertSykmelding(new WSGradertSykmelding()
                                        .withSykmeldingsgrad(50))
                        ),
                new WSPeriode()
                        .withFom(of(2016, 5, 1))
                        .withTom(of(2016, 6, 1))
                        .withAktivitet(new WSAktivitet()
                                .withAntallBehandlingsdagerUke(5)),
                new WSPeriode()
                        .withFom(of(2016, 7, 1))
                        .withTom(of(2016, 8, 1))
                        .withAktivitet(new WSAktivitet()
                                .withHarReisetilskudd(true)),
                new WSPeriode()
                        .withFom(of(2016, 9, 1))
                        .withTom(of(2016, 10, 1))
                        .withAktivitet(new WSAktivitet()
                                .withAvventendeSykmelding("Skikkelig avventende"))

        );
    }
    
}
