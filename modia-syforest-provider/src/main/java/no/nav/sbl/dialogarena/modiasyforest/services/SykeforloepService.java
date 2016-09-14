package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.mapper.SykmeldingMapper;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelse;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSHendelsestype;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSMelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSOppfoelgingstilfelle;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelsestype.valueOf;

public class SykeforloepService {

    @Inject
    private AktoerService aktoerService;

    @Inject
    private SykmeldingV1 sykmeldingV1;

    public List<Sykeforloep> hentSykeforloep(String fnr) {
        String aktoerId = aktoerService.hentAktoerIdForIdent(fnr);

        return sykmeldingV1.hentOppfoelgingstilfelleListe(
                new WSHentOppfoelgingstilfelleListeRequest()
                        .withAktoerId(aktoerId)).getOppfoelgingstilfelleListe().stream()
                .map(tilSykeforloep)
                .collect(toList());
    }

    Function<WSOppfoelgingstilfelle, Sykeforloep> tilSykeforloep = wsOppfoelgingstilfelle ->
            new Sykeforloep()
                    .withHendelser(
                            tilHendelser(wsOppfoelgingstilfelle.getHendelseListe()))
                    .withSykmeldinger(
                            tilSykmeldinger(wsOppfoelgingstilfelle.getMeldingListe()))
                    .withOppfolgingsdato(wsOppfoelgingstilfelle.getOppfoelgingsdato());

    private List<Sykmelding> tilSykmeldinger(List<WSMelding> meldinger) {
        return meldinger
                .stream()
                .map(SykmeldingMapper::sykmelding)
                .collect(toList());
    }

    private List<Hendelse> tilHendelser(List<WSHendelse> hendelser) {
        return hendelser
                .stream()
                .map(wsHendelse ->
                        new Hendelse()
                                .withInntruffetdato(wsHendelse.getDato())
                                .withType(valueOf(wsHendelse.getType().value()))
                                .withTekstkey(fraHendelsetype(wsHendelse.getType()))
                ).collect(toList());
    }

    private String fraHendelsetype(WSHendelsestype type) {
        switch(type){
            case AKTIVITETSKRAV_VARSEL: return "tidslinje.aktivitetskrav-varsel";
            case NY_NAERMESTE_LEDER: return "tidslinje.ny-naermeste-leder";
            default: return null;
        }
    }
}