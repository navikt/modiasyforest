package no.nav.sbl.dialogarena.modiasyforest.mock;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Diagnose;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.MulighetForArbeid;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Periode;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;

public class sykmeldingMock {

    public static List<Sykmelding> sykmeldingerMock() {
        return asList(
                sykmeldingNy(),
                sykmeldingSendt(),
                sykmeldingUtgaatt(),
                sykmeldingAvbrutt()
        );
    }
    private static Diagnose hovedDiagnose() {
        Diagnose diagnose = new Diagnose();
        diagnose.diagnose = "TENDINITT INA";
        diagnose.diagnosekode = "";
        diagnose.diagnosesystem = "";
        return diagnose;
    }

    private static List<Diagnose> biDiagnose() {
        return asList(hovedDiagnose());
    }

    private static Periode periode() {
        Periode periode = new Periode();
        periode.fom = now().minusDays(10);
        periode.tom = now().plusDays(10);
        periode.grad = 100;
        return periode;
    }

    private static MulighetForArbeid mulighetForArbeid() {
        MulighetForArbeid mulighetForArbeid = new MulighetForArbeid();
        mulighetForArbeid.perioder = asList(periode());
        mulighetForArbeid.aktivitetIkkeMulig433 = asList("Annet");
        mulighetForArbeid.aktivitetIkkeMulig434 = asList("Annet");
        mulighetForArbeid.aarsakAktivitetIkkeMulig433 = "andre årsaker til sykefravær";
        mulighetForArbeid.aarsakAktivitetIkkeMulig434 = "andre årsaker til sykefravær";
        return mulighetForArbeid;
    }

    private static Sykmelding sykmeldingBasic() {
        Sykmelding sykmelding =  new Sykmelding();
        sykmelding.mulighetForArbeid = mulighetForArbeid();

        return sykmelding.withId("8d775034-8e3e-4489-9784-52a1e99e0ab5")
                .withStartLegemeldtFravaer(now().minusDays(10))
                .withSkalViseSkravertFelt(true)
                .withIdentdato(now().minusDays(30))
                .withFnr("99008811772")
                .withFornavn("Sygvart")
                .withMellomnavn("Mellomnavn")
                .withEtternavn("Sykmeldt")
                .withInnsendtArbeidsgivernavn("BERGEN TEST HR KONSERN")
                .withOrgnummer("000999000")
                .withArbeidsgiver("LOMMEN BARNEHAVE")
                .withHoveddiagnose(hovedDiagnose())
                .withBidiagnose(biDiagnose())
                .withFravaerBeskrivelse("Medising årsak i kategorien annet")
                .withYrkesskade(false)
                .withSvangerskap(false);
    }

    private static Sykmelding sykmeldingNy() {
        return sykmeldingBasic()
                .withStatus("NY");
    }

    private static Sykmelding sykmeldingSendt() {
        return sykmeldingBasic()
                .withStatus("SENDT")
                .withSendtTilArbeidsgiverDato(LocalDateTime.now());
    }

    private static Sykmelding sykmeldingUtgaatt() {
        return sykmeldingBasic()
                .withStatus("UTGAATT");
    }

    private static Sykmelding sykmeldingAvbrutt() {
        return sykmeldingBasic()
                .withStatus("AVBRUTT");
    }
}
