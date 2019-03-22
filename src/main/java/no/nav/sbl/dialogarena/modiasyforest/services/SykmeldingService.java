package no.nav.sbl.dialogarena.modiasyforest.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.config.SykmeldingerConfig;
import no.nav.sbl.dialogarena.modiasyforest.oidc.OIDCIssuer;
import no.nav.sbl.dialogarena.modiasyforest.controller.domain.sykmelding.Sykmelding;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentSykmeldingListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1;
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentSykmeldingListeRequest;
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentSykmeldingListeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.modiasyforest.mappers.SykmeldingMapper.sykmeldinger;
import static no.nav.sbl.dialogarena.modiasyforest.utils.OIDCUtil.tokenFraOIDC;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class SykmeldingService {

    @Value("${dev}")
    private String dev;
    private OIDCRequestContextHolder contextHolder;
    private AktoerService aktoerService;
    private SykmeldingerConfig sykmeldingerConfig;
    private SykmeldingV1 sykmeldingV1;

    @Inject
    public SykmeldingService(
            OIDCRequestContextHolder contextHolder,
            AktoerService aktoerService,
            SykmeldingerConfig sykmeldingerConfig,
            SykmeldingV1 sykmeldingV1
    ) {
        this.contextHolder = contextHolder;
        this.aktoerService = aktoerService;
        this.sykmeldingerConfig = sykmeldingerConfig;
        this.sykmeldingV1 = sykmeldingV1;
    }

    public List<Sykmelding> hentSykmeldinger(String fnr, List<WSSkjermes> skjermes, String oidcIssuer) {
        if (isBlank(fnr) || !fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente sykmeldinger med fnr");
            throw new IllegalArgumentException();
        }

        String aktoerId = aktoerService.hentAktoerIdForFnr(fnr);

        try {
            WSHentSykmeldingListeRequest request = new WSHentSykmeldingListeRequest()
                    .withAktoerId(aktoerId)
                    .withSkjermes(skjermes);

            List<Sykmelding> sykmeldinger = mapTilSykmeldinger(request, oidcIssuer);
            populerMedArbeidsgivernavn(sykmeldinger);
            return sykmeldinger;
        } catch (HentSykmeldingListeSikkerhetsbegrensning e) {
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            //TODO Skrur av denne logglinjen da den skaper veldig mye støy. Gjøres om når syfoservice tilbyr PIP over HTTP
//            log.error("Noe gikk galt under webservice-kall til syfoservice", e);
            throw new RuntimeException();
        }
    }

    private List<Sykmelding> mapTilSykmeldinger(WSHentSykmeldingListeRequest request, String oidcIssuer) throws HentSykmeldingListeSikkerhetsbegrensning {
        WSHentSykmeldingListeResponse response;
        if ("true".equals(dev)) {
            response = sykmeldingV1.hentSykmeldingListe(request);
        } else {
            String oidcToken = tokenFraOIDC(this.contextHolder, oidcIssuer);
            response = sykmeldingerConfig.hentSykmeldingListe(request, oidcToken);
        }
        return sykmeldinger(response.getMeldingListe());
    }

    @Deprecated
    private void populerMedArbeidsgivernavn(List<Sykmelding> sykmeldinger) {
        sykmeldinger.stream()
                .filter(sykmelding -> sykmelding.mottakendeArbeidsgiver != null)
                .forEach(sykmelding -> sykmelding.innsendtArbeidsgivernavn = sykmelding.mottakendeArbeidsgiver.navn);
    }

    public Sykmelding hentSykmelding(String sykmeldingId, String fnr) {
        return hentSykmeldinger(fnr, emptyList(), OIDCIssuer.INTERN).stream()
                .filter(sykmelding -> sykmelding.id.equals(sykmeldingId))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
