package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype.BOBLE;

public class TidslinjeHendelserService {

    public List<Hendelse> hentHendelser(TidslinjeType type) {
        switch (type) {
            case MED_ARBEIDSGIVER:
                return asList(
                        sykefravaerMedArbeidsgiverStartet(),
                        sykmeldtHvaNaa(),
                        snakkMedArbeidsgiver(),
                        forberedelseDialogmoteArbeidsgiver(),
                        navVurdereKravOmAktivitet(),
                        forberedelseDialogmoteNav(),
                        langtidssykmeldt(),
                        sluttfasenAvSykefravaeret()
                );
            case UTEN_ARBEIDSGIVER:
                return asList(
                        sykefravaerUtenArbeidsgiverStartet(),
                        sykmeldtHvaNaa(),
                        mulighetForAktivitetUtenArbeidsgiver(),
                        snakkMedNav(),
                        aktivitetsplan(),
                        sluttfasenAvSykefravaeret()
                );
            default:
                throw new RuntimeException("Dette bør ikke skje");
        }
    }

    private Hendelse aktivitetsplan() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 39 - 1)
                .withTekstkey("tidslinje.aktivitetsplan");
    }

    private Hendelse sykefravaerMedArbeidsgiverStartet() {
        return new Hendelse()
                .withType(BOBLE)
                .withTekstkey("tidslinje.sykefravaer-startet")
                .withAntallDager(-1);
    }


    private Hendelse sykefravaerUtenArbeidsgiverStartet() {
        return new Hendelse()
                .withType(BOBLE)
                .withTekstkey("tidslinje.sykefravaer-startet")
                .withAntallDager(-1);
    }

    private Hendelse sykmeldtHvaNaa() {
        return new Hendelse()
                .withType(BOBLE)
                .withTekstkey("tidslinje.sykmeldt-hva-naa")
                .withAntallDager(1);
    }

    private Hendelse forberedelseDialogmoteArbeidsgiver() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 7 - 1)
                .withTekstkey("tidslinje.dialogmote-arbeidsgiver");
    }

    private Hendelse forberedelseDialogmoteNav() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 26 - 1)
                .withTekstkey("tidslinje.dialogmote-nav");
    }

    private Hendelse langtidssykmeldt() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 39 - 1)
                .withTekstkey("tidslinje.langtidssykmeldt");
    }

    private Hendelse navVurdereKravOmAktivitet() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 8 - 1)
                .withTekstkey("tidslinje.aktivitetskrav");
    }

    private Hendelse snakkMedArbeidsgiver() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 4 - 1)
                .withTekstkey("tidslinje.snakk-med-arbeidsgiver");
    }

    private Hendelse snakkMedNav() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 12 - 1)
                .withTekstkey("tidslinje.snakk-med-nav");
    }

    private Hendelse sluttfasenAvSykefravaeret() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 52 - 1)
                .withTekstkey("tidslinje.sluttfasen");
    }

    private Hendelse mulighetForAktivitetUtenArbeidsgiver() {
        return new Hendelse()
                .withType(BOBLE)
                .withAntallDager(7 * 8 - 1)
                .withTekstkey("tidslinje.mulighet-for-aktivitet");
    }
}
