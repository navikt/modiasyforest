package no.nav.syfo.services;

import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.controller.domain.NaermesteLeder;
import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.ereg.EregConsumer;
import no.nav.syfo.ereg.Virksomhetsnummer;
import no.nav.syfo.util.DistinctFilter;
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
import static no.nav.syfo.mappers.NaermesteLederMapper.tilNaermesteLeder;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class NaermesteLederService {

    private static final Logger log = getLogger(NaermesteLederService.class);

    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;

    private AktorConsumer aktorConsumer;

    private EregConsumer eregConsumer;

    @Inject
    public NaermesteLederService(
            AktorConsumer aktorConsumer,
            EregConsumer eregConsumer,
            SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1
    ) {
        this.aktorConsumer = aktorConsumer;
        this.eregConsumer = eregConsumer;
        this.sykefravaersoppfoelgingV1 = sykefravaersoppfoelgingV1;
    }

    @Cacheable(cacheNames = "syfoledere", key = "#fnr", condition = "#fnr != null")
    public List<NaermesteLeder> hentNaermesteledere(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente naermesteledere med fnr");
            throw new IllegalArgumentException();
        }
        try {
            return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktorConsumer.hentAktoerIdForFnr(fnr))
                    .withKunAktive(true)).getNaermesteLederListe().stream()
                    .distinct()
                    .map(element -> tilNaermesteLeder(element, eregConsumer.virksomhetsnavn(new Virksomhetsnummer(element.getOrgnummer()))))
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
        String aktoerId = aktorConsumer.hentAktoerIdForFnr(fnr);

        try {
            return sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(false)
            )
                    .getNaermesteLederListe()
                    .stream()
                    .map(element -> tilNaermesteLeder(element, eregConsumer.virksomhetsnavn(new Virksomhetsnummer(element.getOrgnummer()))))
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            log.warn("Fikk sikkerhetsbegrensning ved henting av naermeste ledere for person", e);
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
