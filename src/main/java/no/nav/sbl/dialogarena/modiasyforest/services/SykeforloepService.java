package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.mappers.SykmeldingMapper;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.*;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentSykeforlopperiodeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSSykeforlopperiode;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeResponse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentOppfoelgingstilfelleListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype.valueOf;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class SykeforloepService {

    private static final Logger LOG = getLogger(SykeforloepService.class);

    @Inject
    private AktoerService aktoerService;
    @Inject
    private SykmeldingV1 sykmeldingV1;
    @Inject
    private NaermesteLederService naermesteLederService;
    @Inject
    private OrganisasjonService organisasjonService;
    @Inject
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;

    @Cacheable(value = "syfo", keyGenerator = "userkeygenerator")
    public List<Sykeforloep> hentSykeforloep(String fnr) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente sykeforløp med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }

        String aktoerId = aktoerService.hentAktoerIdForFnr(fnr);

        try {
            return sykmeldingV1.hentOppfoelgingstilfelleListe(
                    new WSHentOppfoelgingstilfelleListeRequest()
                            .withAktoerId(aktoerId)).getOppfoelgingstilfelleListe().stream()
                    .map(wsOppfoelgingstilfelle -> tilSykeforloep(wsOppfoelgingstilfelle, fnr))
                    .collect(toList());
        } catch (HentOppfoelgingstilfelleListeSikkerhetsbegrensning e) {
            LOG.warn("{} fikk sikkerhetsbegrensning ved henting av sykeforloep for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk runtimeexception ved henting av sykeforloep for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
            throw e;
        }
    }

    private Sykeforloep tilSykeforloep(WSOppfoelgingstilfelle wsOppfoelgingstilfelle, String fnr) {
        return new Sykeforloep()
                .withHendelser(tilHendelser(wsOppfoelgingstilfelle.getHendelseListe(), fnr))
                .withSykmeldinger(tilSykmeldinger(wsOppfoelgingstilfelle.getMeldingListe()))
                .withOppfolgingsdato(wsOppfoelgingstilfelle.getOppfoelgingsdato())
                .withSluttdato(sluttdato(tilSykmeldinger(wsOppfoelgingstilfelle.getMeldingListe())));
    }

    public LocalDate sluttdato(List<Sykmelding> sykmeldinger) {
        return sykmeldinger
                .stream()
                .flatMap(sykmelding -> sykmelding.mulighetForArbeid.perioder.stream())
                .sorted((o1, o2) -> o2.tom.compareTo(o1.tom))
                .map(sykmelding -> sykmelding.tom)
                .findFirst().orElseThrow(NotFoundException::new);
    }

    private List<Sykmelding> tilSykmeldinger(List<WSMelding> meldinger) {
        return meldinger
                .stream()
                .map(SykmeldingMapper::sykmelding)
                .collect(toList());
    }

    private List<Hendelse> tilHendelser(List<WSHendelse> hendelser, String fnr) {

        Predicate<WSHendelse> nyNaermesteLederHendelse = h -> h.getType().equals(WSHendelsestype.NY_NAERMESTE_LEDER);
        Predicate<WSHendelse> generellHendelse = nyNaermesteLederHendelse.negate();

        Stream<WSHendelse> nyNaermesteLederHendelser = hendelser.stream().filter(nyNaermesteLederHendelse);
        Stream<WSHendelse> generelleHendelser = hendelser.stream().filter(generellHendelse);

        return concat(
                tilHendelseNyNaermesteLeder(nyNaermesteLederHendelser, fnr),
                tilHendelseGenerell(generelleHendelser)
        ).collect(toList());
    }

    private Stream<Hendelse> tilHendelseGenerell(Stream<WSHendelse> generelleHendelser) {
        return generelleHendelser.map(this::tilHendelse);
    }

    private Stream<Hendelse> tilHendelseNyNaermesteLeder(Stream<WSHendelse> wsHendelseStream, String fnr) {
        try {
            Function<NaermesteLeder, NaermesteLeder> berikMedOrgNavn = nl -> nl.withOrganisasjonsnavn(organisasjonService.hentNavn(nl.orgnummer));

            Map<Long, NaermesteLeder> naermesteLedere = naermesteLederService.finnNarmesteLedere(fnr)
                    .stream()
                    .map(berikMedOrgNavn)
                    .collect(toMap(NaermesteLeder::hentId, leder -> leder, (l1, l2) -> l2));

            return wsHendelseStream.map(wsHendelse -> {
                WSHendelseNyNaermesteLeder wsHendelseNyNaermesteLeder = (WSHendelseNyNaermesteLeder) wsHendelse;
                Long id = wsHendelseNyNaermesteLeder.getNaermesteLederId();

                return tilHendelse(wsHendelse)
                        .withData("naermesteLeder", naermesteLedere.get(id));
            });

        } catch (RuntimeException e) {
            LOG.error("{} fikk runtimeexception ved henting av naermesteledere for person {}", getIdent().orElse("<Ikke funnet>"), fnr, e);
            return empty();
        }
    }

    private Hendelse tilHendelse(WSHendelse wsHendelse) {
        return new Hendelse()
                .withInntruffetdato(wsHendelse.getTidspunkt().toLocalDate())
                .withType(valueOf(wsHendelse.getType().value()))
                .withTekstkey(fraHendelsetype(wsHendelse.getType()));
    }

    private String fraHendelsetype(WSHendelsestype type) {
        switch (type) {
            case AKTIVITETSKRAV_VARSEL:
                return "tidslinje.aktivitetskrav-varsel";
            case NY_NAERMESTE_LEDER:
                return "tidslinje.ny-naermeste-leder";
            default:
                return null;
        }
    }

    public List<Oppfolgingstilfelle> hentOppfolgingstilfelle(String fnr, String orgnummer) {
        String aktorId = aktoerService.hentAktoerIdForFnr(fnr);

        WSHentSykeforlopperiodeRequest request = new WSHentSykeforlopperiodeRequest()
                .withAktoerId(aktorId)
                .withOrgnummer(orgnummer);

        try {
            WSHentSykeforlopperiodeResponse wsHentSykeforlopperiodeResponse = sykefravaersoppfoelgingV1.hentSykeforlopperiode(request);
            return tilSykeforlopperiodeListe(wsHentSykeforlopperiodeResponse.getSykeforlopperiodeListe(), orgnummer);
        } catch (HentSykeforlopperiodeSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved henting av sykeforløpsperioder");
            return emptyList();
        }
    }

    private List<Oppfolgingstilfelle> tilSykeforlopperiodeListe(List<WSSykeforlopperiode> wsSykeforlopperiodeListe, String orgnummer) {
        return wsSykeforlopperiodeListe
                .stream()
                .map(wsSykeforlopperiode -> new Oppfolgingstilfelle()
                        .orgnummer(orgnummer)
                        .fom(wsSykeforlopperiode.getFom())
                        .tom(wsSykeforlopperiode.getTom())
                        .grad(wsSykeforlopperiode.getGrad())
                        .aktivitet(wsSykeforlopperiode.getAktivitet()))
                .collect(toList());
    }
}
