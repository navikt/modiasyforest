package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederListeElement;

public class NaermesteLederMapper {

    public static Naermesteleder tilNaermesteLeder(WSNaermesteLederListeElement response, String organisasjon) {
        return new Naermesteleder()
                .withNavn(response.getNavn())
                .withEpost(response.getEpost())
                .withTlf(response.getMobil())
                .withFomDato(response.getAktivFom())
                .withArbeidsgiver(new Arbeidsgiver()
                        .withOrgnummer(response.getOrgnummer())
                .withNavn(organisasjon));
    }
}
