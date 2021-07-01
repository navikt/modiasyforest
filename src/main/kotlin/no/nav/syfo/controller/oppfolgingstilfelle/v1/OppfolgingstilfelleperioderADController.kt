package no.nav.syfo.controller.oppfolgingstilfelle.v1

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.AZURE
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syketilfelle.SyketilfelleConsumer
import no.nav.syfo.consumer.syketilfelle.domain.toOppfolgingstilfelle
import no.nav.syfo.consumer.syketilfelle.domain.toOppfolgingstilfellePerson
import no.nav.syfo.controller.oppfolgingstilfelle.domain.Oppfolgingstilfelle
import no.nav.syfo.controller.oppfolgingstilfelle.domain.OppfolgingstilfellePerson
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.util.createCallId
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @RequestMapping(value = ["/utenarbeidsgiver"])
    fun getOppfolgingstilfelleperiodeNoArbeidsgiver(
        @RequestParam(value = "fnr") fnr: String
    ): List<OppfolgingstilfellePerson> {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr)

        val callId = createCallId()

        val fodselsnummer = Fodselsnummer(fnr)
        val aktorId = pdlConsumer.aktorId(fodselsnummer, callId)

        return syketilfelleConsumer.getOppfolgingstilfelleNoArbeidsgiver(aktorId, callId)?.toOppfolgingstilfellePerson()?.let {
            listOf(it)
        } ?: emptyList()
    }
}
