package no.nav.syfo.services;

import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.consumer.NaermesteLederConsumer;
import no.nav.syfo.controller.domain.NaermesteLeder;
import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.ereg.EregConsumer;
import no.nav.syfo.ereg.Virksomhetsnummer;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NaermesteLederConsumerTest {

    @Mock
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Mock
    private AktorConsumer aktorConsumer;
    @Mock
    private EregConsumer eregConsumer;
    @InjectMocks
    private NaermesteLederConsumer naermesteLederConsumer;

    @Before
    public void setup() {
        when(aktorConsumer.hentAktoerIdForFnr(anyString())).thenReturn("12345678901");
        when(eregConsumer.virksomhetsnavn(any(Virksomhetsnummer.class))).thenReturn("Testbedriften");
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
                        .withOrgnummer("123456789"),
                new WSNaermesteLeder()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true))
                        .withMobil("123")
                        .withNavn("Navn")
                        .withEpost("test@nav.no")
                        .withOrgnummer("123456789"),
                new WSNaermesteLeder()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                .withErAktiv(true))
                        .withMobil("321")
                        .withNavn("Navn2")
                        .withEpost("test2@nav.no")
                        .withOrgnummer("123456782")

        )));
        List<NaermesteLeder> naermesteledere = naermesteLederConsumer.hentNaermesteledere("12345678901");
        //Henter distincte innslag
        assertThat(naermesteledere.size()).isEqualTo(2);
        assertThat(naermesteledere.get(0).navn).isEqualTo("Navn");

        assertThat(naermesteledere.get(0).organisasjonsnavn).isEqualTo("Testbedriften");
    }

    @Test
    public void hentOrgSomIkkeHarsendtNaermesteLeder() {
        List<NaermesteLeder> naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("2")
                        .withStatus("SENDT")
        ));
        assertThat(naermesteledere.size()).isEqualTo(1);
    }

    @Test
    public void hvisOrgHarSendtBlirIkkeSykmeldingenMed() {
        List<NaermesteLeder> naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(asList(
                new NaermesteLeder().withOrgnummer("1")
        ), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus("SENDT")
        ));
        assertThat(naermesteledere.size()).isEqualTo(0);
    }

    @Test
    public void ikkeSendteBlirIkkeMed() {
        List<NaermesteLeder> naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus("NY")
        ));
        assertThat(naermesteledere.size()).isEqualTo(0);
    }

    @Test
    public void multipleSendteSykmeldingerTilSammeArbeidsgiverBlirBareEn() {
        List<NaermesteLeder> naermesteledere = naermesteLederConsumer.hentOrganisasjonerSomIkkeHarSvart(emptyList(), asList(
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus("SENDT"),
                new Sykmelding()
                        .withOrgnummer("1")
                        .withStatus("SENDT")
        ));
        assertThat(naermesteledere.size()).isEqualTo(1);
    }
}
