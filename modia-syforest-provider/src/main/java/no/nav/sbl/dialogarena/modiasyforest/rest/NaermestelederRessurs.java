package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.metrics.aspects.Timed;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.sbl.dialogarena.modiasyforest.services.NaermesteLederService;
import no.nav.syfo.domain.Arbeidsgiver;
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
@Path("/naermesteleder")
@Produces(APPLICATION_JSON)
public class NaermestelederRessurs {

    @Inject
    private NaermesteLederService naermesteLederService;

    @GET
    @Timed
    public List<Naermesteleder> hentNaermesteledere(@QueryParam("fnr") String fnr) {
//        return naermesteLederService.hentNaermesteledere(fnr);

        return asList(
                new Naermesteleder()
                        .withFodselsdato("071186")
                        .withId(1)
                        .withNavn("Jobi")
                        .withEpost("jobi@test.no")
                        .withTlf("12345678")
                        .withAktiv(true)
                        .withArbeidsgiver(new Arbeidsgiver()
                                .withOrgnummer("***REMOVED***")
                                .withNavn("***REMOVED*** CONSULTING AS")),
                new Naermesteleder()
                        .withFodselsdato("121276")
                        .withId(2)
                        .withNavn("Carl Christian Christensen")
                        .withEpost("ccc@test.no")
                        .withTlf("12345678")
                        .withAktiv(false)
                        .withArbeidsgiver(new Arbeidsgiver()
                                .withOrgnummer("***REMOVED***")
                                .withNavn("***REMOVED*** CONSULTING AS")),
                new Naermesteleder()
                        .withFodselsdato("010456")
                        .withId(3)
                        .withNavn("Alexander Langtmannsnavnsomerlangt")
                        .withEpost("enveldiglangemailadressesomtesterlangestrenger")
                        .withTlf("12345678")
                        .withAktiv(true)
                        .withArbeidsgiver(new Arbeidsgiver()
                                .withOrgnummer("12345678")
                                .withNavn("Arbeids- og Velferdsdirektoratet"))
        );
    }
}
