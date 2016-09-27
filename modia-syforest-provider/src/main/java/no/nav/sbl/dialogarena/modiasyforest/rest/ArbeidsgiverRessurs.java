package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver.Naermesteleder;
import no.nav.sbl.dialogarena.modiasyforest.services.ArbeidsforholdService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/arbeidsgiver")
@Produces(APPLICATION_JSON)
public class ArbeidsgiverRessurs {

    @Inject
    private ArbeidsforholdService arbeidsforholdService;

    @GET
    @Timed
    public List<Arbeidsgiver> hentArbeidsgivere(@QueryParam("fnr") String fnr) {
//        return arbeidsforholdService.hentArbeidsgivere(fnr);

        return asList(
                new Arbeidsgiver()
                        .withNavn("***REMOVED***")
                        .withOrgnummer("***REMOVED***")
                        .withNaermesteledere(asList(
                                new Naermesteleder()
                                        .withNavn("Jobi")
                                        .withEpost("jobi@test.no")
                                        .withTlf("12345678"),
                                new Naermesteleder()
                                        .withNavn("Carl Christian Christensen")
                                        .withEpost("ccc@test.no")
                                        .withTlf("87654321")

                        )),
                new Arbeidsgiver()
                        .withNavn("NAV")
                        .withOrgnummer("12345678")
                        .withNaermesteledere(asList(
                                new Naermesteleder()
                                        .withNavn("Ola Svenskmann Har Et Langt Navn Som Mange Andre Har")
                                        .withEpost("enveldiglangemailadressesomtesterlangestrenger@test.no")
                                        .withTlf("12345678")
                        ))
        );
    }
}
