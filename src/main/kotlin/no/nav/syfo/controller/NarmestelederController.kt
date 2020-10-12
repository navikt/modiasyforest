package no.nav.syfo.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.AZURE
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.narmesteleder.NaermesteLeder
import no.nav.syfo.consumer.narmesteleder.NarmesteLederService
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.metric.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad"])
class NarmestelederController @Inject
constructor(
    private val metrikk: Metrikk,
    private val narmesteLederService: NarmesteLederService,
    private val tilgangConsumer: TilgangConsumer
) {
    @GetMapping(value = ["/allnaermesteledere"], produces = [APPLICATION_JSON])
    fun getAllNarmesteledere(
        @RequestParam(value = "fnr") fnr: String
    ): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_narmesteleder_all")

        tilgangConsumer.throwExceptionIfVeilederWithoutAccess(fnr)

        val naermesteledere = narmesteLederService.narmesteLedere(Fodselsnummer(fnr))
        var idcounter: Long = 0
        for (naermesteleder in naermesteledere) {
            naermesteleder.id = idcounter++
        }
        return naermesteledere
    }
}
