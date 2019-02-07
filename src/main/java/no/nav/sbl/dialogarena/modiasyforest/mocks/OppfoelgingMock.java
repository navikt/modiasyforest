package no.nav.sbl.dialogarena.modiasyforest.mocks;

import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.*;
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
                        .withNavn("Test Trondsen")
                        .withEpost("testTron@nav.no")
                        .withOrgnummer("000321000")
                        .withMobil("12356772")
                        .withArbeidsgiverForskuttererLoenn(true)
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true)
                                .withAktivFom(now()))
        ));
    }

    @Override
    public void ping() {

    }

    @Override
    public WSHentSykeforlopperiodeResponse hentSykeforlopperiode(WSHentSykeforlopperiodeRequest wsHentSykeforlopperiodeRequest) throws HentSykeforlopperiodeSikkerhetsbegrensning {
        return null;
    }

    @Override
    public WSHentNaermesteLedersHendelseListeResponse hentNaermesteLedersHendelseListe(WSHentNaermesteLedersHendelseListeRequest wsHentNaermesteLedersHendelseListeRequest) throws HentNaermesteLedersHendelseListeSikkerhetsbegrensning {
        return null;
    }

    @Override
    public WSBerikNaermesteLedersAnsattBolkResponse berikNaermesteLedersAnsattBolk(WSBerikNaermesteLedersAnsattBolkRequest wsBerikNaermesteLedersAnsattBolkRequest) throws BerikNaermesteLedersAnsattBolkSikkerhetsbegrensning {
        return null;
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
                        .withAktoerId("1112221112221")
                        .withNaermesteLederId(345)
                        .withNavn("Test Testesen")
                        .withOrgnummer("112211221"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivTom(now().minusDays(10)).withAktivFom(now().minusDays(20)).withErAktiv(false))
                        .withAktoerId("2223332223332")
                        .withNaermesteLederId(234)
                        .withNavn("Test Testesen")
                        .withOrgnummer("223322332"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivFom(now().minusDays(10)).withErAktiv(true))
                        .withAktoerId("3334443334443")
                        .withNaermesteLederId(346)
                        .withNavn("Test Testesen")
                        .withOrgnummer("334433443")

        ));
    }
}
