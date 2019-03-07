package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.utils.DistinctFilter;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.NaermesteLederMapper.tilNaermesteLeder;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class NaermesteLederService {

    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;

    private AktoerService aktoerService;

    private OrganisasjonService organisasjonService;

    @Inject
    public NaermesteLederService(
            AktoerService aktoerService,
            OrganisasjonService organisasjonService,
            SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1
    ) {
        this.aktoerService = aktoerService;
        this.organisasjonService = organisasjonService;
        this.sykefravaersoppfoelgingV1 = sykefravaersoppfoelgingV1;
    }

    @Cacheable(cacheNames = "syfoledere", key = "#fnr", condition = "#fnr != null")
    public List<NaermesteLeder> hentNaermesteledere(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Pprøvde å hente naermesteledere med fnr");
            throw new IllegalArgumentException();
        }
        try {
            return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerService.hentAktoerIdForFnr(fnr))
                    .withKunAktive(true)).getNaermesteLederListe().stream()
                    .distinct()
                    .map(element -> tilNaermesteLeder(element, organisasjonService.hentNavn(element.getOrgnummer())))
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            log.warn("Fikk sikkerhetsbegrensning {} ved henting av naermeste ledere for person", e.getFaultInfo().getFeilaarsak().toUpperCase(), e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("Fikk Runtimefeil ved henting av naermeste ledere for person", e);
            throw e;
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

    @Cacheable(cacheNames = "syfofinnledere", key = "#fnr", condition = "#fnr != null")
    public List<NaermesteLeder> finnNarmesteLedere(String fnr) {
        String aktoerId = aktoerService.hentAktoerIdForFnr(fnr);

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
            log.warn("Fikk sikkerhetsbegrensning ved henting av naermeste ledere for person {}", e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("Fikk Runtimefeil ved henting av naermesteledere for person", e);
            throw e;
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
                        .withAktivTom(naermesteLeder.getNaermesteLederStatus().getAktivTom()))
                .orElse(null);
    }
}
