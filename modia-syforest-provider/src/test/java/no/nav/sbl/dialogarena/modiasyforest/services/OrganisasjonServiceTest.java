package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSOrganisasjon;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonServiceTest {

    @Mock
    OrganisasjonV4 organisasjonWebService;

    @InjectMocks
    OrganisasjonService organisasjonService;

    @Before
    public void setUp() throws Exception {
        when(organisasjonWebService.hentOrganisasjon(any())).thenReturn(byggResponse());
    }

    @Test
    public void testAtArbeidsgiversNavnInneholderAlleNavnelinjer() {
        String navn = organisasjonService.hentNavn("123456789");
        assertThat(navn).isEqualTo("NAV AS, Avdeling Økernveien, Rom 5281");
    }

    private WSHentOrganisasjonResponse byggResponse() {
        return new WSHentOrganisasjonResponse()
                .withOrganisasjon(new WSOrganisasjon()
                        .withNavn(new WSUstrukturertNavn()
                                .withNavnelinje("NAV AS")
                                .withNavnelinje("Avdeling Økernveien")
                                .withNavnelinje("Rom 5281")));
    }

}
