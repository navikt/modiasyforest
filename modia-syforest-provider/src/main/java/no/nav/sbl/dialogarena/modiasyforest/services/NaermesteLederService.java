package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoException;
import no.nav.sbl.dialogarena.modiasyforest.rest.feil.SyfoTilgangException;
import no.nav.sbl.dialogarena.modiasyforest.utils.DistinctFilter;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;

import javax.inject.Inject;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.NaermesteLederMapper.tilNaermesteLeder;
import static no.nav.sbl.dialogarena.modiasyforest.rest.feil.Feil.SYKEFORLOEP_INGEN_TILGANG;

public class NaermesteLederService {

    @Inject
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Inject
    private AktoerService aktoerService;
    @Inject
    private OrganisasjonService organisasjonService;

    public List<NaermesteLeder> hentNaermesteledere(String fnr) {
        try {
            return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerService.hentAktoerIdForIdent(fnr))
                    .withKunAktive(true)).getNaermesteLederListe().stream()
                    .distinct()
                    .map(element -> tilNaermesteLeder(element, organisasjonService.hentNavn(element.getOrgnummer())))
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            throw new SyfoTilgangException(e.getFaultInfo().getFeilaarsak().toUpperCase());
        }
    }

    public List<NaermesteLeder> hentOrganisasjonerSomIkkeHarSvart(List<NaermesteLeder> naermesteledere, List<Sykmelding> sykmeldinger) {
        DistinctFilter<Sykmelding, String> distinctFilter = new DistinctFilter<>();
        return sykmeldinger.stream()
                .filter(sykmelding -> "SENDT".equals(sykmelding.status))
                .filter(distinctFilter.on(naermesteleder -> naermesteleder.orgnummer))
                .filter(sykmelding -> naermesteledere.stream()
                        .noneMatch(naermesteleder -> sykmelding.orgnummer.equals(naermesteleder.orgnummer)))
                .map(sykmelding -> new NaermesteLeder()
                        .withOrganisasjonsnavn(sykmelding.innsendtArbeidsgivernavn)
                        .withOrgnummer(sykmelding.orgnummer)
                        .withErOppgitt(false))
                .collect(toList());
    }

    public List<NaermesteLeder> finnNarmesteLedere(String fnr) {
        String aktoerId = aktoerService.hentAktoerIdForIdent(fnr);

        try {
            return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(false)
            )
                    .getNaermesteLederListe()
                    .stream()
                    .map(this::naermesteLeder)
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            throw new SyfoException(SYKEFORLOEP_INGEN_TILGANG);
        }
    }

    private NaermesteLeder naermesteLeder(WSNaermesteLeder naermesteLeder) {
        return ofNullable(naermesteLeder).map(nl ->
                new NaermesteLeder()
                        .withId(naermesteLeder.getNaermesteLederId())
                        .withEpost(naermesteLeder.getEpost())
                        .withTlf(naermesteLeder.getMobil())
                        .withNavn(naermesteLeder.getNavn())
                        .withAktoerId(naermesteLeder.getNaermesteLederAktoerId())
                        .withFomDato(naermesteLeder.getNaermesteLederStatus().getAktivFom())
                        .withOrgnummer(naermesteLeder.getOrgnummer())
                        .withArbeidsgiverForskuttererLoenn(naermesteLeder.isArbeidsgiverForskuttererLoenn())
                        .withFomDato(naermesteLeder.getNaermesteLederStatus().getAktivFom()))
                .orElse(null);
    }
}
