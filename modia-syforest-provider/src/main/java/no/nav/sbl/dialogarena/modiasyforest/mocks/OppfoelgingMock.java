package no.nav.sbl.dialogarena.modiasyforest.mocks;

import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentHendelseListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSAnsatt;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederStatus;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*;

import java.util.Arrays;

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
    public WSHentHendelseListeResponse hentHendelseListe(WSHentHendelseListeRequest request) throws HentHendelseListeSikkerhetsbegrensning {
        return null;
    }

    @Override
    public WSHentNaermesteLedersAnsattListeResponse hentNaermesteLedersAnsattListe(WSHentNaermesteLedersAnsattListeRequest request) {
        return new WSHentNaermesteLedersAnsattListeResponse().withAnsattListe(Arrays.asList(
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivFom(now().minusDays(10)).withErAktiv(true))
                        .withAktoerId("***REMOVED***")
                        .withNaermesteLederId(345)
                        .withNavn("Test Testesen")
                        .withOrgnummer("***REMOVED***"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivTom(now().minusDays(10)).withAktivFom(now().minusDays(20)).withErAktiv(false))
                        .withAktoerId("***REMOVED***")
                        .withNaermesteLederId(234)
                        .withNavn("Test Testesen")
                        .withOrgnummer("***REMOVED***"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivFom(now().minusDays(10)).withErAktiv(true))
                        .withAktoerId("***REMOVED***")
                        .withNaermesteLederId(346)
                        .withNavn("Test Testesen")
                        .withOrgnummer("***REMOVED***")

        ));
    }
}
