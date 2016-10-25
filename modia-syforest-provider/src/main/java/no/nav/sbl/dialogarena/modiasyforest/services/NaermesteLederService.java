package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Arbeidsgiver;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Naermesteleder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

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
                .distinct()
                .map(element -> tilNaermesteLeder(element,
                        organisasjonService.hentNavn(element.getOrgnummer()),
                        fnr.substring(0, 6)))
                .collect(toList());
    }

    public List<Naermesteleder> hentOrganisasjonerSomIkkeHarSvart(List<Naermesteleder> naermesteledere, List<Sykmelding> sykmeldinger) {
        return sykmeldinger.stream()
                .filter(sykmelding -> "SENDT".equals(sykmelding.status))
                .filter(sykmelding -> !naermesteledere.stream()
                        .filter(naermesteleder -> sykmelding.orgnummer.equals(naermesteleder.arbeidsgiver.orgnummer))
                        .findAny()
                        .isPresent())
                .map(sykmelding -> new Naermesteleder()
                        .withArbeidsgiver(new Arbeidsgiver()
                                .withOrgnummer(sykmelding.orgnummer)
                                .withNavn(sykmelding.innsendtArbeidsgivernavn))
                        .withNavn("Ikke meldt inn")
                        .withEpost("Ikke meldt inn")
                        .withFodselsdato("Ikke meldt inn")
                        .withTlf("Ikke meldt inn"))
                .collect(toList());


    }
}
