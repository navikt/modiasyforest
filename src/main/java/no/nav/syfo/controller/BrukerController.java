package no.nav.syfo.controller;

import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.services.DkifService;
import no.nav.syfo.services.TilgangService;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Produces;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.syfo.mappers.BrukerMapper.ws2bruker;
import static no.nav.syfo.mock.brukerMock.brukerMedAdresser;
import static no.nav.syfo.oidc.OIDCIssuer.INTERN;
import static no.nav.syfo.utils.MapUtil.map;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/brukerinfo")
@Produces(APPLICATION_JSON)
public class BrukerController {

    private BrukerprofilService brukerprofilService;
    private DkifService dkifService;
    private TilgangService tilgangsKontroll;

    @Inject
    public BrukerController(
            final BrukerprofilService brukerprofilService,
            final DkifService dkifService,
            final TilgangService tilgangsKontroll
    ) {
        this.brukerprofilService = brukerprofilService;
        this.dkifService = dkifService;
        this.tilgangsKontroll = tilgangsKontroll;
    }

    @ProtectedWithClaims(issuer = INTERN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Bruker hentNavn(@RequestParam(value = "fnr") String fnr) {
        tilgangsKontroll.sjekkVeiledersTilgangTilPerson(fnr);

        if ("true".equals(getProperty("local.mock"))) {
            return brukerMedAdresser();
        }
        return map(brukerprofilService.hentBruker(fnr), ws2bruker)
                .kontaktinfo(dkifService.hentKontaktinfoFnr(fnr, OIDCIssuer.INTERN))
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
