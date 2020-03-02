package no.nav.syfo.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.NaermesteLederService
import no.nav.syfo.services.TilgangService
import no.nav.syfo.utils.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/naermesteleder"])
class NarmestelederController @Inject
constructor(
        private val metrikk: Metrikk,
        private val naermesteLederService: NaermesteLederService,
        private val tilgangService: TilgangService
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getNarmesteledere(@RequestParam(value = "fnr") fnr: String): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_naermesteledere")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)


        val naermesteledere = naermesteLederService.hentNaermesteledere(fnr)
        var idcounter: Long = 0
        for (naermesteleder in naermesteledere) {
            naermesteleder.id = idcounter++
        }
        return naermesteledere
    }
}
