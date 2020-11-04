package no.nav.syfo.controller.oppfolgingstilfelle

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.AZURE
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syketilfelle.SyketilfelleConsumer
import no.nav.syfo.consumer.syketilfelle.domain.toOppfolgingstilfelle
import no.nav.syfo.controller.oppfolgingstilfelle.domain.Oppfolgingstilfelle
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.util.createCallId
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/oppfolgingstilfelleperioder"])
class OppfolgingstilfelleperioderADController @Inject constructor(
    private val tilgangsKontroll: TilgangConsumer,
    private val pdlConsumer: PdlConsumer,
    private val syketilfelleConsumer: SyketilfelleConsumer
) {
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOppfolgingstilfelleperioder(
        @RequestParam(value = "fnr") fnr: String,
        @RequestParam(value = "orgnummer") orgnummer: String
    ): List<Oppfolgingstilfelle> {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr)

        val callId = createCallId()

        val fodselsnummer = Fodselsnummer(fnr)
        val aktorId = pdlConsumer.aktorId(fodselsnummer, callId)

        return syketilfelleConsumer.getOppfolgingstilfelle(aktorId, orgnummer, callId)?.toOppfolgingstilfelle()?.let {
            listOf(it)
        } ?: emptyList()
    }
}
