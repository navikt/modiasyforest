package no.nav.syfo.mocks;

import no.nav.syfo.config.AAregConfig;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = AAregConfig.MOCK_KEY, havingValue = "true")
public class ArbeidsforholdMock implements ArbeidsforholdV3 {
    public FinnArbeidsforholdPrArbeidsgiverResponse finnArbeidsforholdPrArbeidsgiver(FinnArbeidsforholdPrArbeidsgiverRequest parameters)
            throws FinnArbeidsforholdPrArbeidsgiverForMangeForekomster, FinnArbeidsforholdPrArbeidsgiverSikkerhetsbegrensning, FinnArbeidsforholdPrArbeidsgiverUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public FinnArbeidsforholdPrArbeidstakerResponse finnArbeidsforholdPrArbeidstaker(FinnArbeidsforholdPrArbeidstakerRequest parameters)
            throws FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning, FinnArbeidsforholdPrArbeidstakerUgyldigInput {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        Organisasjon arbeidsgiver = new Organisasjon();
        arbeidsgiver.setOrgnummer("000321000");
        arbeidsforhold.setArbeidsgiver(arbeidsgiver);
        FinnArbeidsforholdPrArbeidstakerResponse response = new FinnArbeidsforholdPrArbeidstakerResponse();
        response.getArbeidsforhold().add(arbeidsforhold);
        return response;
    }

    public HentArbeidsforholdHistorikkResponse hentArbeidsforholdHistorikk(HentArbeidsforholdHistorikkRequest parameters)
            throws HentArbeidsforholdHistorikkArbeidsforholdIkkeFunnet, HentArbeidsforholdHistorikkSikkerhetsbegrensning {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public FinnArbeidstakerePrArbeidsgiverResponse finnArbeidstakerePrArbeidsgiver(FinnArbeidstakerePrArbeidsgiverRequest parameters)
            throws FinnArbeidstakerePrArbeidsgiverSikkerhetsbegrensning, FinnArbeidstakerePrArbeidsgiverUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public void ping() { }
}
