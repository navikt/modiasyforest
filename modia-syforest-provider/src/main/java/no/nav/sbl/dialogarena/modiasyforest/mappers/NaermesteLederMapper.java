package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederListeElement;
import static org.apache.commons.lang3.text.WordUtils.capitalize;
public class NaermesteLederMapper {

    public static NaermesteLeder tilNaermesteLeder(WSNaermesteLederListeElement response, String organisasjon) {
        return new NaermesteLeder()
                .withNavn(capitalize(response.getNavn()))
                .withEpost(response.getEpost())
                .withTlf(response.getMobil())
                .withFomDato(response.getAktivFom())
                .withOrgnummer(response.getOrgnummer())
                .withOrganisasjonsnavn(organisasjon)
                .withErOppgitt(true);
    }
}
