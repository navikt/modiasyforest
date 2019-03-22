package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Periode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static no.nav.sbl.dialogarena.modiasyforest.config.CacheConfig.CACHENAME_ARBEIDSFORHOLD;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class ArbeidsforholdService {

    private static final Regelverker A_ORDNINGEN = new Regelverker();

    static {
        A_ORDNINGEN.setValue("A_ORDNINGEN");
    }

    private ArbeidsforholdV3 arbeidsforholdV3;
    private SykmeldingService sykmeldingService;
    private OrganisasjonService organisasjonService;

    @Inject
    public ArbeidsforholdService(
            ArbeidsforholdV3 arbeidsforholdV3,
            SykmeldingService sykmeldingService,
            OrganisasjonService organisasjonService
    ) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.sykmeldingService = sykmeldingService;
        this.organisasjonService = organisasjonService;
    }

    @Cacheable(cacheNames = CACHENAME_ARBEIDSFORHOLD, key = "#fnr.concat(#sykmeldingId)", condition = "#fnr != null && #sykmeldingId != null")
    public List<Arbeidsgiver> hentArbeidsgivere(String fnr, String sykmeldingId) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente arbeidsforhold med fnr");
            throw new IllegalArgumentException();
        }
        Sykmelding sykmelding = sykmeldingService.hentSykmelding(sykmeldingId, fnr);

        try {
            FinnArbeidsforholdPrArbeidstakerRequest request = lagArbeidsforholdRequest(fnr, sykmelding.identdato);
            return arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request).getArbeidsforhold().stream()
                    .filter(a -> a.getArbeidsgiver() instanceof Organisasjon)
                    .map(a -> {
                        String orgnummer = ((Organisasjon) a.getArbeidsgiver()).getOrgnummer();
                        String navn = organisasjonService.hentNavn(orgnummer);

                        return new Arbeidsgiver()
                                .withNavn(navn)
                                .withOrgnummer(orgnummer);
                    }).distinct().collect(toList());

        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput e) {
            log.error("Fikk en feil ved henting av arbeidsforhold for fnr", e);
            throw new IllegalArgumentException();
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            log.error("Fikk en feil ved henting av arbeidsforhold for fnr", e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("Fikk en feil ved henting av arbeidsforhold for fnr", e);
            throw new RuntimeException();
        }
    }

    private FinnArbeidsforholdPrArbeidstakerRequest lagArbeidsforholdRequest(String fnr, LocalDate identdato) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
        request.setRapportertSomRegelverk(A_ORDNINGEN);
        request.setArbeidsforholdIPeriode(periode(identdato));
        request.setIdent(ident(fnr));
        return request;
    }

    private NorskIdent ident(String fodselsnummer) {
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fodselsnummer);
        return ident;
    }

    /**
     * identdato 11.06.2015 12:30 får da søkeintervall: 11.02.2015 - ved midnatt til kvelden.
     */
    private Periode periode(LocalDate identdato) {
        Periode periode = new Periode();
        periode.setFom(convertToXmlGregorianCalendar(identdato.minusMonths(4)));
        periode.setTom(convertToXmlGregorianCalendar(now().plusDays(1)));
        return periode;
    }

    private XMLGregorianCalendar convertToXmlGregorianCalendar(LocalDate dato) {
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(dato.atStartOfDay(ZoneId.systemDefault()));
            return newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException dce) {
            throw new RuntimeException(dce);
        }
    }
}
