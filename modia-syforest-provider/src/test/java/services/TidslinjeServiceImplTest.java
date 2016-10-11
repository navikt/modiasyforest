package services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Tidslinje;
import no.nav.sbl.dialogarena.modiasyforest.services.SykeforloepService;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeHendelserService;
import no.nav.sbl.dialogarena.modiasyforest.services.TidslinjeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype.AKTIVITETSKRAV_VARSEL;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype.NY_NAERMESTE_LEDER;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType.MED_ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType.UTEN_ARBEIDSGIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TidslinjeServiceImplTest {

    @Mock
    private SykeforloepService sykeforloepService;
    @Mock
    private TidslinjeHendelserService tidslinjeHendelserService;
    @InjectMocks
    private TidslinjeService tidslinjeService;

    @Test
    public void hentTidslinjer() throws Exception {
        when(tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER)).thenReturn(asList(new Hendelse()));
        when(tidslinjeHendelserService.hentHendelser(UTEN_ARBEIDSGIVER)).thenReturn(asList(new Hendelse(), new Hendelse()));
        when(sykeforloepService.hentSykeforloep(anyString())).thenReturn(asList(
                new Sykeforloep()
                        .withHendelser(emptyList())
                        .withOppfolgingsdato(now().minusDays(20))
                        .withSykmeldinger(singletonList(new Sykmelding()))));

        List<Tidslinje> uten_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "UTEN_ARBEIDSGIVER");
        assertThat(uten_arbeidsgiver.get(0).hendelser.size()).isEqualTo(2);
        assertThat(uten_arbeidsgiver.get(0).startdato).isEqualTo(now().minusDays(20));

        List<Tidslinje> med_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "MED_ARBEIDSGIVER");
        assertThat(med_arbeidsgiver.get(0).hendelser.size()).isEqualTo(1);
        assertThat(med_arbeidsgiver.get(0).startdato).isEqualTo(now().minusDays(20));
    }

    @Test
    public void setterAntallDagerKorrektPaaHendelseFraSyfoservice() throws Exception {
        when(tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER)).thenReturn(emptyList());
        when(sykeforloepService.hentSykeforloep(anyString())).thenReturn(asList(
                new Sykeforloep()
                        .withHendelser(asList(new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL)))
                        .withOppfolgingsdato(now().minusDays(20))
                        .withSykmeldinger(asList(new Sykmelding()))));
        List<Tidslinje> med_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "MED_ARBEIDSGIVER");
        assertThat(med_arbeidsgiver.get(0).hendelser.get(0).antallDager).isEqualTo(10);
    }

    @Test
    public void setterIDVedAktivitetsvarsel() throws Exception {
        when(tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER)).thenReturn(emptyList());
        when(sykeforloepService.hentSykeforloep(anyString())).thenReturn(asList(
                new Sykeforloep()
                        .withHendelser(asList(new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL)))
                        .withOppfolgingsdato(now().minusDays(20))
                        .withSykmeldinger(asList(new Sykmelding()))));
        List<Tidslinje> med_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "MED_ARBEIDSGIVER");
        assertThat(med_arbeidsgiver.get(0).hendelser.get(0).id).isEqualTo("a1");
    }

    @Test
    public void setterIDVedHendelser() throws Exception {
        when(tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER)).thenReturn(emptyList());
        when(sykeforloepService.hentSykeforloep(anyString())).thenReturn(asList(
                new Sykeforloep()
                        .withHendelser(asList(
                                new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL),
                                new Hendelse().withInntruffetdato(now().minusDays(5)).withType(NY_NAERMESTE_LEDER)))
                        .withOppfolgingsdato(now().minusDays(20))
                        .withSykmeldinger(asList(new Sykmelding()))));
        List<Tidslinje> med_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "MED_ARBEIDSGIVER");
        assertThat(med_arbeidsgiver.get(0).hendelser.get(0).id).isEqualTo("a1");
        assertThat(med_arbeidsgiver.get(0).hendelser.get(1).id).isEqualTo("nl2");
    }


    @Test
    public void sisteForest() throws Exception {
        when(tidslinjeHendelserService.hentHendelser(MED_ARBEIDSGIVER)).thenReturn(emptyList());
        when(sykeforloepService.hentSykeforloep(anyString())).thenReturn(asList(
                new Sykeforloep()
                        .withHendelser(asList(new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL)))
                        .withOppfolgingsdato(of(2016,1,20))
                        .withSykmeldinger(asList(new Sykmelding())),
                new Sykeforloep()
                        .withHendelser(asList(new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL)))
                        .withOppfolgingsdato(of(2016,9,28))
                        .withSykmeldinger(asList(new Sykmelding())),
                new Sykeforloep()
                        .withHendelser(asList(new Hendelse().withInntruffetdato(now().minusDays(10)).withType(AKTIVITETSKRAV_VARSEL)))
                        .withOppfolgingsdato(of(2016,4,1))
                        .withSykmeldinger(asList(new Sykmelding()))));
        List<Tidslinje> med_arbeidsgiver = tidslinjeService.hentTidslinjer("12345678901", "MED_ARBEIDSGIVER");
        assertThat(med_arbeidsgiver.get(0).startdato).isEqualTo(of(2016,9,28));
    }
}