package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.config.consumer.SykmeldingerConfig;
import no.nav.syfo.consumer.AktorConsumer;
import no.nav.syfo.controller.domain.*;
import no.nav.syfo.controller.domain.sykmelding.Sykmelding;
import no.nav.syfo.controller.domain.tidslinje.Hendelse;
import no.nav.syfo.controller.domain.tidslinje.Hendelsestype;
import no.nav.syfo.ereg.EregConsumer;
import no.nav.syfo.ereg.Virksomhetsnummer;
import no.nav.syfo.mappers.SykmeldingMapper;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentSykeforlopperiodeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSSykeforlopperiode;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeResponse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentOppfoelgingstilfelleListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import static no.nav.syfo.oidc.OIDCUtil.tokenFraOIDC;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class OppfolgingstilfelleService {

    private static final Logger log = getLogger(OppfolgingstilfelleService.class);

    @Value("${dev}")
    private String dev;
    private OIDCRequestContextHolder contextHolder;
    private AktorConsumer aktorConsumer;
    private EregConsumer eregConsumer;
    private NaermesteLederService naermesteLederService;
    private SykmeldingerConfig sykmeldingerConfig;
    private SykmeldingV1 sykmeldingV1;
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;

    @Inject
    public OppfolgingstilfelleService(
            OIDCRequestContextHolder contextHolder,
            AktorConsumer aktorConsumer,
            EregConsumer eregConsumer,
            NaermesteLederService naermesteLederService,
            SykmeldingerConfig sykmeldingerConfig,
            SykmeldingV1 sykmeldingV1,
            SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1
    ) {
        this.contextHolder = contextHolder;
        this.aktorConsumer = aktorConsumer;
        this.eregConsumer = eregConsumer;
        this.naermesteLederService = naermesteLederService;
        this.sykmeldingerConfig = sykmeldingerConfig;
        this.sykmeldingV1 = sykmeldingV1;
        this.sykefravaersoppfoelgingV1 = sykefravaersoppfoelgingV1;
    }

    public List<Sykeforloep> getOppfolgingstilfelle(String fnr, String oidcIssuer) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente sykeforløp med fnr og oidcIssuer {}");
            throw new IllegalArgumentException();
        }

        String aktoerId = aktorConsumer.hentAktoerIdForFnr(fnr);

        try {
            WSHentOppfoelgingstilfelleListeRequest request = new WSHentOppfoelgingstilfelleListeRequest()
                    .withAktoerId(aktoerId);
            WSHentOppfoelgingstilfelleListeResponse response;
            if ("true".equals(dev)) {
                response = sykmeldingV1.hentOppfoelgingstilfelleListe(request);
            } else {
                String oidcToken = tokenFraOIDC(this.contextHolder, oidcIssuer);
                response = sykmeldingerConfig.hentOppfoelgingstilfelleListe(request, oidcToken);
            }

            return response.getOppfoelgingstilfelleListe()
                    .stream()
                    .map(wsOppfoelgingstilfelle -> tilSykeforloep(wsOppfoelgingstilfelle, fnr))
                    .collect(toList());
        } catch (HentOppfoelgingstilfelleListeSikkerhetsbegrensning e) {
            log.warn("Fikk sikkerhetsbegrensning ved henting av oppfolgingstilfelleListe for person", e);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("Fikk runtimeexception ved henting av oppfolgingstilfelleListe for person", e);
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
            Function<NaermesteLeder, NaermesteLeder> berikMedOrgNavn = nl -> nl.withOrganisasjonsnavn(
                    eregConsumer.virksomhetsnavn(new Virksomhetsnummer(nl.orgnummer))
            );

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
            log.error("Fikk runtimeexception ved henting av naermesteledere for person med fnr", e);
            return empty();
        }
    }

    private Hendelse tilHendelse(WSHendelse wsHendelse) {
        return new Hendelse()
                .withInntruffetdato(wsHendelse.getTidspunkt().toLocalDate())
                .withType(Hendelsestype.valueOf(wsHendelse.getType().value()))
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

    public List<Oppfolgingstilfelle> hentOppfolgingstilfelleperioder(String fnr, String orgnummer) {
        String aktorId = aktorConsumer.hentAktoerIdForFnr(fnr);

        WSHentSykeforlopperiodeRequest request = new WSHentSykeforlopperiodeRequest()
                .withAktoerId(aktorId)
                .withOrgnummer(orgnummer);

        try {
            WSHentSykeforlopperiodeResponse wsHentSykeforlopperiodeResponse = sykefravaersoppfoelgingV1.hentSykeforlopperiode(request);
            return tilSykeforlopperiodeListe(wsHentSykeforlopperiodeResponse.getSykeforlopperiodeListe(), orgnummer);
        } catch (HentSykeforlopperiodeSikkerhetsbegrensning e) {
            log.warn("Sikkerhetsbegrensning ved henting av oppfølgingstilfelleperioder");
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
