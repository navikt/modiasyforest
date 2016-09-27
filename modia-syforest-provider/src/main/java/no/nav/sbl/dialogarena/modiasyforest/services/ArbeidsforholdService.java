package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feilmelding.Feil.ARBEIDSFORHOLD_GENERELL_FEIL;
import static org.slf4j.LoggerFactory.getLogger;

public class ArbeidsforholdService {

    private static final Logger LOG = getLogger(ArbeidsforholdService.class);
    private static final Regelverker A_ORDNINGEN = new Regelverker();

    static {
        A_ORDNINGEN.setValue("A_ORDNINGEN");
    }

    @Inject
    private NaermesteLederService naermesteLederService;
    @Inject
    private ArbeidsforholdV3 arbeidsforholdV3;
    @Inject
    private OrganisasjonService organisasjonService;

    /**
     * *
     * Bruk Cacheable her, ikke spring aop. For at det skal fungere må request/response ha implementert hashcode og equals,
     * det gjør den ikke før vi få kjørt en egen tjenestespesifikasjon, og det gjør vi ikke pdd. pga en feil i .WSDL'en (brukt name="parameters")
     * på både request og respons type. Det er rapportert til KES og når det er fikset kan denne fjernes og legges i cacheconfig.xml istedenfor.
     *
     */
    @Cacheable("arbeidsforhold")
    public List<Arbeidsgiver> hentArbeidsgivere(String fnr) {

        try {
            FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
            request.setRapportertSomRegelverk(A_ORDNINGEN);
            request.setIdent(ident(fnr));

            return arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request).getArbeidsforhold().stream()
                    .filter(a -> a.getArbeidsgiver() instanceof Organisasjon)
                    .map(a -> {
                        String orgnummer = ((Organisasjon) a.getArbeidsgiver()).getOrgnummer();
                        String navn = organisasjonService.hentNavn(orgnummer);

                        return new Arbeidsgiver()
                                .withNavn(navn)
                                .withOrgnummer(orgnummer);

                    }).collect(toList());

        } catch (Exception e) {
            LOG.error("Feil ved henting av arbeidsforhold", e);
            throw new SyfoException(ARBEIDSFORHOLD_GENERELL_FEIL);
        }
    }

    private NorskIdent ident(String fodselsnummer) {
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fodselsnummer);
        return ident;
    }
}
