package no.nav.syfo.controller.oppfolgingstilfelle.v2

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.INTERN_AZUREAD_V2
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syketilfelle.SyketilfelleConsumer
import no.nav.syfo.consumer.syketilfelle.domain.toOppfolgingstilfelle
import no.nav.syfo.consumer.syketilfelle.domain.toOppfolgingstilfellePerson
import no.nav.syfo.controller.oppfolgingstilfelle.domain.Oppfolgingstilfelle
import no.nav.syfo.controller.oppfolgingstilfelle.domain.OppfolgingstilfellePerson
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import no.nav.syfo.util.createCallId
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = INTERN_AZUREAD_V2)
@RequestMapping(value = ["/api/internad/v2/oppfolgingstilfelleperioder"])
class OppfolgingstilfelleperioderControllerV2 @Inject constructor(
    private val tilgangsKontroll: TilgangConsumer,
    private val pdlConsumer: PdlConsumer,
    private val syketilfelleConsumer: SyketilfelleConsumer
) {
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOppfolgingstilfelleperioder(
        @RequestParam(value = "fnr") fnr: String?,
        @RequestParam(value = "orgnummer") orgnummer: String,
        @RequestHeader headers: MultiValueMap<String, String>
    ): List<Oppfolgingstilfelle> {
        val requestedPersonIdent = fnr ?: headers.getFirst(NAV_PERSONIDENT_HEADER)

        if (requestedPersonIdent.isNullOrEmpty()) {
            throw IllegalArgumentException("Did not find a PersonIdent in request headers or in Request param")
        } else {
            tilgangsKontroll.throwExceptionIfVeilederWithoutAccessOBO(requestedPersonIdent)

            val callId = createCallId()

            val fodselsnummer = Fodselsnummer(requestedPersonIdent)
            val aktorId = pdlConsumer.aktorId(fodselsnummer, callId)

            return syketilfelleConsumer.getOppfolgingstilfelle(aktorId, orgnummer, callId)?.toOppfolgingstilfelle()?.let {
                listOf(it)
            } ?: emptyList()
        }
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @RequestMapping(value = ["/utenarbeidsgiver"])
    fun getOppfolgingstilfelleperiodeNoArbeidsgiver(
        @RequestParam(value = "fnr") fnr: String?,
        @RequestHeader headers: MultiValueMap<String, String>
    ): List<OppfolgingstilfellePerson> {
        val requestedPersonIdent = fnr ?: headers.getFirst(NAV_PERSONIDENT_HEADER)

        if (requestedPersonIdent.isNullOrEmpty()) {
            throw IllegalArgumentException("Did not find a PersonIdent in request headers or in Request param")
        } else {
            tilgangsKontroll.throwExceptionIfVeilederWithoutAccessOBO(requestedPersonIdent)

            val callId = createCallId()

            val fodselsnummer = Fodselsnummer(requestedPersonIdent)
            val aktorId = pdlConsumer.aktorId(fodselsnummer, callId)

            return syketilfelleConsumer.getOppfolgingstilfelleNoArbeidsgiver(aktorId, callId)?.toOppfolgingstilfellePerson()?.let {
                listOf(it)
            } ?: emptyList()
        }
    }
}
