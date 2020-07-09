package no.nav.syfo.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.consumer.AktorConsumer
import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.narmesteleder.NarmesteLederConsumer
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.NaermesteLederService
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.metric.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad"])
class NarmestelederController @Inject
constructor(
    private val aktorConsumer: AktorConsumer,
    private val metrikk: Metrikk,
    private val naermesteLederService: NaermesteLederService,
    private val narmesteLederConsumer: NarmesteLederConsumer,
    private val tilgangConsumer: TilgangConsumer
) {

    @GetMapping(value = ["/naermesteleder"], produces = [APPLICATION_JSON])
    fun getNarmesteledere(@RequestParam(value = "fnr") fnr: String): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_naermesteledere")

        tilgangConsumer.throwExceptionIfVeilederWithoutAccess(fnr)


        val naermesteledere = naermesteLederService.hentNaermesteledere(fnr)
        var idcounter: Long = 0
        for (naermesteleder in naermesteledere) {
            naermesteleder.id = idcounter++
        }
        return naermesteledere
    }

    @GetMapping(value = ["/allnaermesteledere"], produces = [APPLICATION_JSON])
    fun getAllNarmesteledere(@RequestParam(value = "fnr") fnr: String): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_narmesteleder_all")

        tilgangConsumer.throwExceptionIfVeilederWithoutAccess(fnr)

        val naermesteledere = naermesteLederService.finnNarmesteLedere(fnr)
        var idcounter: Long = 0
        for (naermesteleder in naermesteledere) {
            naermesteleder.id = idcounter++
        }
        return naermesteledere
    }
}
