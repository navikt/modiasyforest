package no.nav.syfo.controller.narmesteleder

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.AZURE
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.narmesteleder.NarmesteLederService
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.springframework.util.MultiValueMap
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
        @RequestParam(value = "fnr") fnr: String?,
        @RequestHeader headers: MultiValueMap<String, String>
    ): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_narmesteleder_all")
        val requestedPersonIdent: String? = fnr ?: headers.getFirst(NAV_PERSONIDENT_HEADER)

        if (requestedPersonIdent.isNullOrEmpty()) {
            throw IllegalArgumentException("Did not find a PersonIdent in request headers or in Request param")
        } else {
            tilgangConsumer.throwExceptionIfVeilederWithoutAccess(requestedPersonIdent)

            val naermesteledere = narmesteLederService.narmesteLedere(Fodselsnummer(requestedPersonIdent))
            var idcounter: Long = 0
            for (naermesteleder in naermesteledere) {
                naermesteleder.id = idcounter++
            }
            return naermesteledere
        }
    }
}
