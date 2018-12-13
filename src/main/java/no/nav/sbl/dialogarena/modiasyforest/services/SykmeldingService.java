package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentSykmeldingListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentSykmeldingListeRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.SykmeldingMapper.sykmeldinger;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class SykmeldingService {

    private static final Logger LOG = getLogger(SykmeldingService.class);

    @Inject
    private AktoerService aktoerService;
    @Inject
    private SykmeldingV1 sykmeldingV1;

    @Cacheable(value = "sykmelding", keyGenerator = "userkeygenerator")
    public List<Sykmelding> hentSykmeldinger(String fnr, List<WSSkjermes> skjermes) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente sykmeldinger med fnr {}", getIdent().orElse("<Ikke funnet>"), fnr);
            throw new IllegalArgumentException();
        }

        String aktoerId = aktoerService.hentAktoerIdForFnr(fnr);

        try {
            WSHentSykmeldingListeRequest request = new WSHentSykmeldingListeRequest()
                    .withAktoerId(aktoerId)
                    .withSkjermes(skjermes);

            List<Sykmelding> sykmeldinger = mapTilSykmeldinger(request);
            populerMedArbeidsgivernavn(sykmeldinger);
            return sykmeldinger;
        } catch (HentSykmeldingListeSikkerhetsbegrensning e) {
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            //TODO Skrur av denne logglinjen da den skaper veldig mye støy. Gjøres om når syfoservice tilbyr PIP over HTTP
//            LOG.error("Noe gikk galt under webservice-kall til syfoservice", e);
            throw new RuntimeException();
        }
    }

    private List<Sykmelding> mapTilSykmeldinger(WSHentSykmeldingListeRequest request) throws HentSykmeldingListeSikkerhetsbegrensning {
        return sykmeldinger(sykmeldingV1.hentSykmeldingListe(request).getMeldingListe());
    }

    @Deprecated
    private void populerMedArbeidsgivernavn(List<Sykmelding> sykmeldinger) {
        sykmeldinger.stream()
                .filter(sykmelding -> sykmelding.mottakendeArbeidsgiver != null)
                .forEach(sykmelding -> sykmelding.innsendtArbeidsgivernavn = sykmelding.mottakendeArbeidsgiver.navn);
    }

    public Sykmelding hentSykmelding(String sykmeldingId, String fnr) {
        return hentSykmeldinger(fnr, emptyList()).stream()
                .filter(sykmelding -> sykmelding.id.equals(sykmeldingId))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
