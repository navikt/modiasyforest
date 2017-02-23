package no.nav.sbl.dialogarena.modiasyforest.mocks;

import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederStatus;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;

public class OppfoelgingMock implements SykefravaersoppfoelgingV1 {
    @Override
    public WSHentNaermesteLederListeResponse hentNaermesteLederListe(WSHentNaermesteLederListeRequest request) {
        return new WSHentNaermesteLederListeResponse().withNaermesteLederListe(asList(
                new WSNaermesteLeder()
                        .withNavn("***REMOVED***")
                        .withEpost("trond@nav.no")
                        .withOrgnummer("***REMOVED***")
                        .withMobil("12356772")
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true)
                                .withAktivFom(now()))
        ));
    }

    @Override
    public void ping() {

    }

    @Override
    public WSHentNaermesteLederResponse hentNaermesteLeder(WSHentNaermesteLederRequest request) {
        throw new RuntimeException("Ikke implmentert. Se OppfoelgingMock");
    }

    @Override
    public WSHentNaermesteLedersAnsattListeResponse hentNaermesteLedersAnsattListe(WSHentNaermesteLedersAnsattListeRequest request) {
        return null;
    }
}
