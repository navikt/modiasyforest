package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.utils.DistinctFilter;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederListeElement;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.NaermesteLederMapper.tilNaermesteLeder;

public class NaermesteLederService {

    @Inject
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Inject
    private AktoerService aktoerService;
    @Inject
    private OrganisasjonService organisasjonService;

    public List<NaermesteLeder> hentNaermesteledere(String fnr) {
        return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                .withAktoerId(aktoerService.hentAktoerIdForIdent(fnr))
                .withKunAktive(true)).getNaermesteLederListe().stream()
                .distinct()
                .map(element -> tilNaermesteLeder(element, organisasjonService.hentNavn(element.getOrgnummer())))
                .collect(toList());
    }

    public List<NaermesteLeder> hentOrganisasjonerSomIkkeHarSvart(List<NaermesteLeder> naermesteledere, List<Sykmelding> sykmeldinger) {
        DistinctFilter<Sykmelding, String> distinctFilter = new DistinctFilter<>();
        return sykmeldinger.stream()
                .filter(sykmelding -> "SENDT".equals(sykmelding.status))
                .filter(distinctFilter.on(naermesteleder -> naermesteleder.orgnummer))
                .filter(sykmelding -> !naermesteledere.stream()
                        .filter(naermesteleder -> sykmelding.orgnummer.equals(naermesteleder.orgnummer))
                        .findAny()
                        .isPresent())
                .map(sykmelding -> new NaermesteLeder()
                        .withOrganisasjonsNavn(sykmelding.innsendtArbeidsgivernavn)
                        .withOrgnummer(sykmelding.orgnummer)
                        .withErOppgitt(false))
                .collect(toList());
    }

    public Optional<NaermesteLeder> finnNaermesteLeder(String orgnummer, String fnr) {
        String aktoerId = aktoerService.hentAktoerIdForIdent(fnr);

        WSHentNaermesteLederResponse wsHentNaermesteLederResponse = sykefravaersoppfoelgingV1.hentNaermesteLeder(
                new WSHentNaermesteLederRequest()
                        .withAktoerId(aktoerId)
                        .withOrgnummer(orgnummer)
        );

        return ofNullable(wsHentNaermesteLederResponse.getNaermesteLeder())
                .filter(WSNaermesteLeder::isAktiv)
                .map(this::naermesteLeder);
    }

    public List<NaermesteLeder> finnNarmesteLedere(String fnr) {
        String aktoerId = aktoerService.hentAktoerIdForIdent(fnr);

        return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                .withAktoerId(aktoerId)
                .withKunAktive(false)
        )
                .getNaermesteLederListe()
                .stream()
                .map(this::naermesteLeder)
                .collect(toList());
    }

    private NaermesteLeder naermesteLeder(WSNaermesteLederListeElement naermesteLeder) {
        return new NaermesteLeder()
                .withId(Long.valueOf(naermesteLeder.getNaermesteLederId()))
                .withEpost(naermesteLeder.getEpost())
                .withTlf(naermesteLeder.getMobil())
                .withNavn(naermesteLeder.getNavn())
                .withOrgnummer(naermesteLeder.getOrgnummer());
    }

    private NaermesteLeder naermesteLeder(WSNaermesteLeder naermesteLeder) {
        return ofNullable(naermesteLeder).map(nl ->
                new NaermesteLeder()
                        .withEpost(naermesteLeder.getEpost())
                        .withTlf(naermesteLeder.getMobil())
                        .withNavn(naermesteLeder.getNavn()))
                .orElse(null);
    }
}
