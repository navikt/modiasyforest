package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Periode;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSMelding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.of;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.SykmeldingMapper.sykmelding;
import static org.assertj.core.api.Assertions.assertThat;
import static no.nav.sbl.dialogarena.modiasyforest.testutils.SykmeldingMocks.getWSSykmelding;

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
                .withSykmelding(getWSSykmelding());
    }


}