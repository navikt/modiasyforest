package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.NaermesteLederMapper.tilNaermesteLeder;

public class NaermesteLederService {

    @Inject
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Inject
    private AktoerService aktoerService;
    @Inject
    private OrganisasjonService organisasjonService;

    public List<Naermesteleder> hentNaermesteledere(String fnr) {

        return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                .withAktoerId(aktoerService.hentAktoerIdForIdent(fnr))
                .withKunAktive(true)).getNaermesteLederListe().stream()
                .map(element -> tilNaermesteLeder(element,
                        organisasjonService.hentNavn(element.getOrgnummer()),
                        fnr.substring(0, 6)))
                .collect(toList());
    }
}
