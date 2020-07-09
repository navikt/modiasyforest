package no.nav.syfo.controller.internad;

import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.controller.domain.Kontaktinfo;
import no.nav.syfo.dkif.DigitalKontaktinfo;
import no.nav.syfo.dkif.DkifConsumer;
import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.consumer.TilgangConsumer;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static no.nav.syfo.oidc.OIDCIssuer.AZURE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = "/api/internad/brukerinfo")
public class UserController {

    private final BrukerprofilService brukerprofilService;
    private final DkifConsumer dkifConsumer;
    private final TilgangConsumer tilgangsKontroll;

    @Inject
    public UserController(
            BrukerprofilService brukerprofilService,
            DkifConsumer dkifConsumer,
            TilgangConsumer tilgangsKontroll
    ) {
        this.brukerprofilService = brukerprofilService;
        this.dkifConsumer = dkifConsumer;
        this.tilgangsKontroll = tilgangsKontroll;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Bruker getUser(@RequestParam(value = "fnr") String fnr) {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr);

        DigitalKontaktinfo digitalKontaktinfo = dkifConsumer.kontaktinformasjon(fnr);
        Kontaktinfo kontaktinfo = new Kontaktinfo()
                .fnr(fnr)
                .skalHaVarsel(digitalKontaktinfo.getKanVarsles())
                .epost(digitalKontaktinfo.getEpostadresse())
                .tlf(digitalKontaktinfo.getMobiltelefonnummer());

        return brukerprofilService.hentBruker(fnr)
                .kontaktinfo(kontaktinfo)
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
