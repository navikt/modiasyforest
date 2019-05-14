package no.nav.syfo.mappers;

import no.nav.syfo.controller.domain.NaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;

import static org.apache.commons.lang3.text.WordUtils.capitalize;
public class NaermesteLederMapper {

    public static NaermesteLeder tilNaermesteLeder(WSNaermesteLeder response, String organisasjon) {
        return new NaermesteLeder()
                .withNavn(capitalize(response.getNavn()))
                .withEpost(response.getEpost())
                .withTlf(response.getMobil())
                .withFomDato(response.getNaermesteLederStatus().getAktivFom())
                .withOrgnummer(response.getOrgnummer())
                .withOrganisasjonsnavn(organisasjon)
                .withAktivTom(response.getNaermesteLederStatus().getAktivTom())
                .withArbeidsgiverForskuttererLoenn(response.isArbeidsgiverForskuttererLoenn())
                .withErOppgitt(true);
    }
}
