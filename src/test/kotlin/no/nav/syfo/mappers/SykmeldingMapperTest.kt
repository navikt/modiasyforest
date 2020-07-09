package no.nav.syfo.mappers

import no.nav.syfo.testhelper.SykmeldingMocks.getWSSykmelding
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
class SykmeldingMapperTest {
    @Test
    @Throws(Exception::class)
    fun testMappingenFraWebserviceobjektene() {
        val sykmelding = SykmeldingMapper.sykmelding(meldingWS())

        Assertions.assertThat(sykmelding.id).isNotNull()
        Assertions.assertThat(sykmelding.arbeidsgiver).isNotEmpty()
        Assertions.assertThat(sykmelding.diagnose.hoveddiagnose).isNotNull
        Assertions.assertThat(sykmelding.pasient.etternavn).isNotEmpty()
        Assertions.assertThat(sykmelding.pasient.fnr).isNotEmpty()
        Assertions.assertThat(sykmelding.pasient.fornavn).isNotEmpty()
        Assertions.assertThat(sykmelding.bekreftelse.sykmelder).isNotEmpty()
        Assertions.assertThat(sykmelding.diagnose.fravaersgrunnLovfestet).isNotEmpty()
        Assertions.assertThat(sykmelding.diagnose.fravaerBeskrivelse).isNotEmpty()
        Assertions.assertThat(sykmelding.mulighetForArbeid.perioder).isNotEmpty

        val perioder = sykmelding.mulighetForArbeid.perioder
        val aktivitetIkkeMulig = perioder[0]
        val gradert = perioder[1]
        val behandlingsdager = perioder[2]
        val reisetilskudd = perioder[3]
        val avventende = perioder[4]

        Assertions.assertThat(aktivitetIkkeMulig.fom).isNotNull()
        Assertions.assertThat(aktivitetIkkeMulig.tom).isNotNull()
        Assertions.assertThat(aktivitetIkkeMulig.grad).isEqualTo(100)
        Assertions.assertThat(gradert.grad).isGreaterThan(0).isLessThan(100)
        Assertions.assertThat(behandlingsdager.behandlingsdager).isGreaterThan(0)
        Assertions.assertThat(reisetilskudd.reisetilskudd).isTrue()
        Assertions.assertThat(avventende.avventende).isNotEmpty()
        Assertions.assertThat(sykmelding.mulighetForArbeid.aktivitetIkkeMulig433).isNotEmpty
        Assertions.assertThat(sykmelding.mulighetForArbeid.aarsakAktivitetIkkeMulig433).isNotEmpty()
        Assertions.assertThat(sykmelding.bekreftelse.utstedelsesdato).isNotNull()
        Assertions.assertThat(sykmelding.sporsmal.arbeidssituasjon).isEqualTo("ARBEIDSTAKER")
        Assertions.assertThat(sykmelding.sporsmal.fravaersperioder[0].fom).isEqualTo(LocalDate.of(2018, 10, 17))
        Assertions.assertThat(sykmelding.sporsmal.fravaersperioder[0].tom).isEqualTo(LocalDate.of(2018, 10, 17))
    }

    @Throws(Exception::class)
    private fun meldingWS(): WSMelding {
        return WSMelding()
            .withMeldingId("1")
            .withStatus("NY")
            .withSendtTilArbeidsgiverDato(LocalDateTime.of(2016, 4, 4, 12, 40))
            .withIdentdato(LocalDate.of(2016, 4, 4))
            .withSykmelding(getWSSykmelding())
            .withSmSpoersmaal(
                WSSMSpoersmaal()
                    .withHarAnnetFravaer(true)
                    .withForsikringsgrad(60)
                    .withHarForsikringsgrad(true)
                    .withArbeidssituasjon(WSArbeidssituasjon.ARBEIDSTAKER)
                    .withAnnenFravaersperiodeListe(WSDatospenn()
                        .withFom(LocalDate.of(2018, 10, 17))
                        .withTom(LocalDate.of(2018, 10, 17))))
    }
}
