package no.nav.sbl.dialogarena.modiasyforest.mock;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Tidslinje;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;

public class tidslinjeMock {

    public static List<Tidslinje> tidslinjeMock(String type) {
        if (type.equals("UTEN_ARBEIDSGIVER")) {
            return asList(
                    new Tidslinje().withStartDato(now().minusDays(28))
                            .withHendelser(hendelserUtenArbgiver())
            );
        }
        return asList(
                new Tidslinje().withStartDato(now().minusDays(28))
                        .withHendelser(hendelserMedArbgiver())
        );
    }

    private static Hendelse hendelseBoble() {
        return new Hendelse()
                .withType(Hendelsestype.BOBLE);
    }

    private static Hendelse hendelseNL() {
        return new Hendelse()
                .withType(Hendelsestype.NY_NAERMESTE_LEDER)
                .withData("naermesteLeder", naermesteLeder());
    }

    private static NaermesteLeder naermesteLeder() {
        return new NaermesteLeder()
                .withNavn("Are Arbeidsgiver")
                .withId(1L)
                .withAktoerId("12345678")
                .withTlf("12345678")
                .withEpost("test@test.no")
                .withFomDato(now().minusDays(28))
                .withOrganisasjonsnavn("LOMMEN BARNEHAVE")
                .withOrgnummer("***REMOVED***");
    }

    private static List<Hendelse> hendelserUtenArbgiver() {
        return asList(
                hendelseNL()
                        .withId("nl1")
                        .withAntallDager(18)
                        .withInntruffetdato(now().minusDays(8))
                        .withTekstkey("tidslinje.ny-naermeste-leder.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(-1)
                        .withTekstkey("tidslinje.sykefravaer-startet.UTEN_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(1)
                        .withTekstkey("tidslinje.sykmeldt-hva-naa__modia.UTEN_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(55)
                        .withTekstkey("tidslinje.mulighet-for-aktivitet.UTEN_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(83)
                        .withTekstkey("tidslinje.snakk-med-nav.UTEN_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(272)
                        .withTekstkey("tidslinje.aktivitetsplan.UTEN_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(363)
                        .withTekstkey("tidslinje.sluttfasen.UTEN_ARBEIDSGIVER")
        );
    }

    private static List<Hendelse> hendelserMedArbgiver() {
        return asList(
                hendelseNL()
                        .withId("nl1")
                        .withAntallDager(18)
                        .withInntruffetdato(now().minusDays(8))
                        .withTekstkey("tidslinje.ny-naermeste-leder.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(-1)
                        .withTekstkey("tidslinje.sykefravaer-startet.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(1)
                        .withTekstkey("tidslinje.sykmeldt-hva-naa__modia.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(27)
                        .withTekstkey("tidslinje.snakk-med-arbeidsgiver.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(48)
                        .withTekstkey("tidslinje.dialogmote-arbeidsgiver.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(55)
                        .withTekstkey("tidslinje.aktivitetskrav.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(181)
                        .withTekstkey("tidslinje.dialogmote-nav.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(272)
                        .withTekstkey("tidslinje.langtidssykmeldt.MED_ARBEIDSGIVER"),
                hendelseBoble()
                        .withAntallDager(363)
                        .withTekstkey("tidslinje.sluttfasen.MED_ARBEIDSGIVER")
        );
    }
}
