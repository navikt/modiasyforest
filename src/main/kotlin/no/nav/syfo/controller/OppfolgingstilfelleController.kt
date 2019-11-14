package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.Sykeforloep
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.OppfolgingstilfelleService
import no.nav.syfo.services.TilgangService
import no.nav.syfo.utils.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/oppfolgingstilfelle"])
class OppfolgingstilfelleController @Inject
constructor(
        private val metrikk: Metrikk,
        private val oppfolgingstilfelleService: OppfolgingstilfelleService,
        private val tilgangService: TilgangService
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getOppfolgingstilfeller(@RequestParam(value = "fnr") fnr: String): List<Sykeforloep> {
        metrikk.tellEndepunktKall("get_oppfolgingstilfeller")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)

        return oppfolgingstilfelleService.getOppfolgingstilfelle(fnr, AZURE)
    }
}
