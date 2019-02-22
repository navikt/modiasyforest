package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.NaermesteLeder;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.utils.DistinctFilter;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.NaermesteLederMapper.tilNaermesteLeder;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class NaermesteLederService {
    private static final Logger LOG = getLogger(NaermesteLederService.class);

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

    @Cacheable(value = "syfo")
    public List<NaermesteLeder> hentNaermesteledere(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente naermesteledere med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
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
            LOG.warn("{} fikk sikkerhetsbegrensning {} ved henting av naermeste ledere for person {}", getIdent().orElse("<Ikke funnet>"), e.getFaultInfo().getFeilaarsak().toUpperCase(), fnr, e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk Runtimefeil ved henting av naermeste ledere for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
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

    @Cacheable(value = "syfo")
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
            LOG.warn("{} fikk sikkerhetsbegrensning ved henting av naermeste ledere for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk Runtimefeil ved henting av naermesteledere for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
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
