package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Periode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class ArbeidsforholdService {

    private static final Logger LOG = getLogger(ArbeidsforholdService.class);
    private static final Regelverker A_ORDNINGEN = new Regelverker();

    static {
        A_ORDNINGEN.setValue("A_ORDNINGEN");
    }

    @Inject
    private SykmeldingService sykmeldingService;
    @Inject
    private ArbeidsforholdV3 arbeidsforholdV3;
    @Inject
    private OrganisasjonService organisasjonService;

    @Cacheable(value = "arbeidsforhold", keyGenerator = "userkeygenerator")
    public List<Arbeidsgiver> hentArbeidsgivere(String fnr, String sykmeldingId) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente arbeidsforhold med fnr {}", getSubjectHandler().getUid(), fnr);
            throw new SyfoException(IKKE_FOEDSELSNUMMER);
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
            LOG.error("{} fikk en feil ved henting av arbeidsforhold for fnr {}", getSubjectHandler().getUid(), fnr, e);
            throw new SyfoException(ARBEIDSFORHOLD_UGYLDIG_INPUT);
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            LOG.error("{} fikk en feil ved henting av arbeidsforhold for fnr {}", getSubjectHandler().getUid(), fnr, e);
            throw new SyfoException(ARBEIDSFORHOLD_INGEN_TILGANG);
        } catch (RuntimeException e) {
            LOG.error("{} fikk en feil ved henting av arbeidsforhold for fnr {}", getSubjectHandler().getUid(), fnr, e);
            throw new SyfoException(ARBEIDSFORHOLD_GENERELL_FEIL);
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
