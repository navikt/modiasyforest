package no.nav.syfo.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.Sykeforloep
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.consumer.OppfolgingstilfelleConsumer
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.metric.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/oppfolgingstilfelle"])
class OppfolgingstilfelleController @Inject
constructor(
    private val metrikk: Metrikk,
    private val oppfolgingstilfelleConsumer: OppfolgingstilfelleConsumer,
    private val tilgangConsumer: TilgangConsumer
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getOppfolgingstilfeller(@RequestParam(value = "fnr") fnr: String): List<Sykeforloep> {
        metrikk.tellEndepunktKall("get_oppfolgingstilfeller")

        tilgangConsumer.throwExceptionIfVeilederWithoutAccess(fnr)

        return oppfolgingstilfelleConsumer.getOppfolgingstilfelle(fnr, AZURE)
    }
}
