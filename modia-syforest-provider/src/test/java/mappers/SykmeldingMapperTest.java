package mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Periode;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.SykmeldingMapper.sykmelding;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SykmeldingMapperTest {

    @Test
    public void testMappingenFraWebserviceobjektene() throws Exception {
        Sykmelding sykmelding = sykmelding(meldingWS());

        assertThat(sykmelding.id).isNotNull();
        assertThat(sykmelding.arbeidsgiver).isNotEmpty();
        assertThat(sykmelding.diagnose.hoveddiagnose).isNotNull();
        assertThat(sykmelding.pasient.etternavn).isNotEmpty();
        assertThat(sykmelding.pasient.fnr).isNotEmpty();
        assertThat(sykmelding.pasient.fornavn).isNotEmpty();
        assertThat(sykmelding.bekreftelse.sykmelder).isNotEmpty();

        assertThat(sykmelding.diagnose.fravaersgrunnLovfestet).isNotEmpty();
        assertThat(sykmelding.diagnose.fravaerBeskrivelse).isNotEmpty();

        assertThat(sykmelding.mulighetForArbeid.perioder).isNotEmpty();
        List<Periode> perioder = sykmelding.mulighetForArbeid.perioder;
        Periode aktivitetIkkeMulig = perioder.get(0);
        Periode gradert = perioder.get(1);
        Periode behandlingsdager = perioder.get(2);
        Periode reisetilskudd = perioder.get(3);
        Periode avventende = perioder.get(4);

        assertThat(aktivitetIkkeMulig.fom).isNotNull();
        assertThat(aktivitetIkkeMulig.tom).isNotNull();
        assertThat(aktivitetIkkeMulig.grad).isEqualTo(100);
        assertThat(gradert.grad).isGreaterThan(0).isLessThan(100);
        assertThat(behandlingsdager.behandlingsdager).isGreaterThan(0);
        assertThat(reisetilskudd.reisetilskudd).isTrue();
        assertThat(avventende.avventende).isNotEmpty();

        assertThat(sykmelding.mulighetForArbeid.aktivitetIkkeMulig433).isNotEmpty();
        assertThat(sykmelding.mulighetForArbeid.aarsakAktivitetIkkeMulig433).isNotEmpty();

        assertThat(sykmelding.bekreftelse.utstedelsesdato).isNotNull();
    }

    private WSMelding meldingWS() throws Exception {
        return new WSMelding()
                .withMeldingId("1")
                .withStatus("NY")
                .withSendtTilArbeidsgiverDato(LocalDateTime.of(2016, 4, 4, 12, 40))
                .withIdentdato(of(2016, 4, 4))
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
                );
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