package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.controller.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArbeidsforholdServiceTest {

    @Mock
    SykmeldingService sykmeldingService;
    @Mock
    ArbeidsforholdV3 arbeidsforholdV3;
    @Mock
    OrganisasjonService organisasjonService;

    @InjectMocks
    ArbeidsforholdService arbeidsforholdService;

    @Before
    public void setUp() throws Exception {
        when(arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(anyObject())).thenReturn(byggResponse("123456789"));
        when(sykmeldingService.hentSykmelding(anyString(), anyString())).thenReturn(new Sykmelding().withId("ID:ABC123").withIdentdato(now()));
        when(organisasjonService.hentNavn("123456789")).thenReturn("TEST AS, Avd. Øst");
    }

    @Test
    public void testHentArbeidsgivereSkalReturnereEnListeMedEnArbeidsgiverDersomBrukerenHarEttArbeidsforhold() {
        List<Arbeidsgiver> arbeidsgivere = arbeidsforholdService.hentArbeidsgivere("12345678901", "ID:ABC123");
        assertThat(arbeidsgivere).isNotEmpty();

        Arbeidsgiver arbeidsgiver = arbeidsgivere.get(0);
        assertThat(arbeidsgiver.navn).isEqualTo("TEST AS, Avd. Øst");
        assertThat(arbeidsgiver.orgnummer).isEqualTo("123456789");
    }

    @Test
    public void testHentArbeidsgivereSkalHenteForAordningenOgPeriodenIdentdatoOg4mndBakover() throws Exception {
        LocalDate identdato = now();
        arbeidsforholdService.hentArbeidsgivere("12345678901", "sykmeldingId");
        ArgumentCaptor<FinnArbeidsforholdPrArbeidstakerRequest> request = ArgumentCaptor.forClass(FinnArbeidsforholdPrArbeidstakerRequest.class);
        verify(arbeidsforholdV3).finnArbeidsforholdPrArbeidstaker(request.capture());

        verify(sykmeldingService, times(1)).hentSykmelding("sykmeldingId", "12345678901");
        assertThat(request.getValue().getRapportertSomRegelverk().getValue()).isEqualTo("A_ORDNINGEN");
        assertThat(request.getValue().getArbeidsforholdIPeriode().getFom().toGregorianCalendar()).isEqualByComparingTo(convertToXmlGregorianCalendar(identdato.minusMonths(4)).toGregorianCalendar());
        assertThat(request.getValue().getArbeidsforholdIPeriode().getTom().toGregorianCalendar()).isEqualByComparingTo(convertToXmlGregorianCalendar(identdato.plusDays(1)).toGregorianCalendar());
    }

    @Test
    public void tidspunktBlirSattTilMidnatt() throws FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning, FinnArbeidsforholdPrArbeidstakerUgyldigInput {
        LocalDate identdato = now().atTime(12, 12).toLocalDate();
        arbeidsforholdService.hentArbeidsgivere("12345678901", "sykmeldingId");
        ArgumentCaptor<FinnArbeidsforholdPrArbeidstakerRequest> request = ArgumentCaptor.forClass(FinnArbeidsforholdPrArbeidstakerRequest.class);
        verify(arbeidsforholdV3).finnArbeidsforholdPrArbeidstaker(request.capture());

        assertThat(request.getValue().getArbeidsforholdIPeriode().getTom().getHour()).isEqualByComparingTo(0);
        assertThat(request.getValue().getArbeidsforholdIPeriode().getTom().getMinute()).isEqualByComparingTo(0);
    }

    @Test
    public void ignorerArbeidsforholdHvisArbeidsgiverErPerson() throws Exception {
        when(arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(anyObject())).thenReturn(personSomArbeidsgiver());
        List<Arbeidsgiver> arbeidsgivere = arbeidsforholdService.hentArbeidsgivere("12345678901", "personSomArbeidsgiver");

        assertThat(arbeidsgivere).isEmpty();
    }

    @Test
    public void duplikateArbeidsgivereSkalFiltreresBort() throws Exception {
        FinnArbeidsforholdPrArbeidstakerResponse response = byggResponse("999999999");
        response.getArbeidsforhold().add(byggArbeidsforhold("888888888", now().minusMonths(2), now().minusMonths(1)));
        response.getArbeidsforhold().add(byggArbeidsforhold("888888888", now().minusMonths(1), now().minusMonths(0)));
        when(arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(any())).thenReturn(response);

        List<Arbeidsgiver> arbeidsgivere = arbeidsforholdService.hentArbeidsgivere("12345678901", "fjernet");

        assertThat(arbeidsgivere).hasSize(2);
        assertThat(arbeidsgivere).extracting("orgnummer").contains("888888888", "999999999");
    }

    private FinnArbeidsforholdPrArbeidstakerResponse personSomArbeidsgiver() {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        Person arbeidsgiver = new Person();
        arbeidsforhold.setArbeidsgiver(arbeidsgiver);

        FinnArbeidsforholdPrArbeidstakerResponse response = new FinnArbeidsforholdPrArbeidstakerResponse();
        response.getArbeidsforhold().add(arbeidsforhold);
        return response;
    }

    private FinnArbeidsforholdPrArbeidstakerResponse byggResponse(String orgnr) throws Exception {
        FinnArbeidsforholdPrArbeidstakerResponse response = new FinnArbeidsforholdPrArbeidstakerResponse();
        response.getArbeidsforhold().add(byggArbeidsforhold(orgnr));
        return response;
    }

    private Arbeidsforhold byggArbeidsforhold(String orgnr) {
        return byggArbeidsforhold(orgnr, of(2015, 1, 1), now().plusDays(1));
    }

    private Arbeidsforhold byggArbeidsforhold(String orgnr, LocalDate fom, LocalDate tom) {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        Organisasjon arbeidsgiver = new Organisasjon();
        arbeidsgiver.setOrgnummer(orgnr);
        arbeidsforhold.setArbeidsgiver(arbeidsgiver);

        AnsettelsesPeriode ansettelsesperiode = new AnsettelsesPeriode();
        Gyldighetsperiode periode = new Gyldighetsperiode();
        periode.setFom(convertToXmlGregorianCalendar(fom));
        periode.setTom(convertToXmlGregorianCalendar(tom));
        ansettelsesperiode.setPeriode(periode);
        arbeidsforhold.setAnsettelsesPeriode(ansettelsesperiode);

        return arbeidsforhold;
    }

    private XMLGregorianCalendar convertToXmlGregorianCalendar(LocalDate dato) {
        try {
            return newInstance().newXMLGregorianCalendar(dato.toString());
        } catch (DatatypeConfigurationException dce) {
            throw new RuntimeException(dce);
        }
    }
}
