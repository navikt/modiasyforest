package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.syfo.domain.Arbeidsgiver;
import no.nav.syfo.services.AktoerService;
import no.nav.syfo.services.OrganisasjonService;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederListeElement;

public class NaermesteLederMapper {

    public static Naermesteleder tilNaermesteLeder(WSNaermesteLederListeElement response, OrganisasjonService organisasjonService, AktoerService aktoerService) {
        return new Naermesteleder()
                .withFodselsdato(aktoerService.hentFnrForAktoer(response.getNaermesteLederAktoerId().substring(0, 6)))
                .withNavn(response.getNavn())
                .withEpost(response.getEpost())
                .withTlf(response.getMobil())
                .withArbeidsgiver(new Arbeidsgiver()
                        .withOrgnummer(response.getOrgnummer())
                .withNavn(organisasjonService.hentNavn(response.getOrgnummer())));
    }
}
