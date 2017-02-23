package services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederStatus;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSStatus.NY;
import static no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSStatus.SENDT;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class NaermesteLederServiceTest {

    @Mock
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Mock
    private AktoerService aktoerService;
    @Mock
    private OrganisasjonService organisasjonService;
    @InjectMocks
    private NaermesteLederService naermesteLederService;

    @Before
    public void setup() {
        when(aktoerService.hentAktoerIdForIdent(anyString())).thenReturn("12345678901");
        when(organisasjonService.hentNavn(anyString())).thenReturn("Testbedriften");
        when(aktoerService.hentFnrForAktoer(anyString())).thenReturn("10987654321");
    }

    @Test
    public void henterNaermesteledere() throws HentNaermesteLederListeSikkerhetsbegrensning {
        when(sykefravaersoppfoelgingV1.hentNaermesteLederListe(any())).thenReturn(new WSHentNaermesteLederListeResponse().withNaermesteLederListe(asList(
                new WSNaermesteLeder()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true))
                        .withMobil("123")
                        .withNavn("Navn")
                        .withEpost("test@nav.no")
                        .withOrgnummer("12345678"),
                new WSNaermesteLeder()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true))
                        .withMobil("123")
                        .withNavn("Navn")
                        .withEpost("test@nav.no")
                        .withOrgnummer("12345678"),
                new WSNaermesteLeder()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true))
                        .withMobil("321")
                        .withNavn("Navn2")
                        .withEpost("test2@nav.no")
                        .withOrgnummer("123456782")

        )));
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentNaermesteledere("12345678901");
        //Henter distincte innslag
        assertThat(naermesteledere.size()).isEqualTo(2);
        assertThat(naermesteledere.get(0).navn).isEqualTo("Navn");

        assertThat(naermesteledere.get(0).organisasjonsnavn).isEqualTo("Testbedriften");
    }

    @Test
    public void hentOrgSomIkkeHarsendtNaermesteLeder() {
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("2")
                        .withStatus(SENDT)
        ));
        assertThat(naermesteledere.size()).isEqualTo(1);
    }

    @Test
    public void hvisOrgHarSendtBlirIkkeSykmeldingenMed() {
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(asList(
                new NaermesteLeder().withOrgnummer("1")
        ), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus(SENDT)
        ));
        assertThat(naermesteledere.size()).isEqualTo(0);
    }

    @Test
    public void ikkeSendteBlirIkkeMed() {
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus(NY)
        ));
        assertThat(naermesteledere.size()).isEqualTo(0);
    }

    @Test
    public void multipleSendteSykmeldingerTilSammeArbeidsgiverBlirBareEn() {
        List<NaermesteLeder> naermesteledere = naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus(SENDT),
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus(SENDT)
        ));
        assertThat(naermesteledere.size()).isEqualTo(1);
    }
}
