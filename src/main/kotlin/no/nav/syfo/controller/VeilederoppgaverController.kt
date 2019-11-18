package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.consumer.veilederoppgaver.Veilederoppgave
import no.nav.syfo.consumer.veilederoppgaver.VeilederoppgaverConsumer
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.TilgangService
import no.nav.syfo.utils.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/veilederoppgaver"])
class VeilederoppgaverController @Inject
constructor(
        private val metrikk: Metrikk,
        private val tilgangService: TilgangService,
        private val veilederoppgaverConsumer: VeilederoppgaverConsumer
) {

    @GetMapping(path = ["/{fnr}"], produces = [APPLICATION_JSON])
    fun getVeilederoppgaver(@PathVariable("fnr") fnr: String): List<Veilederoppgave> {
        metrikk.tellEndepunktKall("get_veilederoppgaver")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)

        return veilederoppgaverConsumer.getVeilederoppgaver(fnr)
    }
}
