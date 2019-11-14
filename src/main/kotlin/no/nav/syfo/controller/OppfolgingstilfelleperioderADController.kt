package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.Oppfolgingstilfelle
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.SykeforloepService
import no.nav.syfo.services.TilgangService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/oppfolgingstilfelleperioder"])
class OppfolgingstilfelleperioderADController @Inject
constructor(
        private val tilgangsKontroll: TilgangService,
        private val sykeforloepService: SykeforloepService
) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOppfolgingstilfelleperioder(
            @RequestParam(value = "fnr") fnr: String,
            @RequestParam(value = "orgnummer") orgnummer: String
    ): List<Oppfolgingstilfelle> {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr)

        return sykeforloepService.hentOppfolgingstilfelleperioder(fnr, orgnummer)
    }
}
