package no.nav.syfo.controller.user.v2

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.INTERN_AZUREAD_V2
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.dkif.DigitalKontaktinfo
import no.nav.syfo.consumer.dkif.DkifConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.fullName
import no.nav.syfo.controller.user.domain.Bruker
import no.nav.syfo.controller.user.domain.Kontaktinfo
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = INTERN_AZUREAD_V2)
@RequestMapping(value = ["/api/internad/v2/brukerinfo"])
class UserControllerV2 @Inject constructor(
    private val dkifConsumer: DkifConsumer,
    private val pdlConsumer: PdlConsumer,
    private val tilgangsKontroll: TilgangConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUser(
        @RequestParam(value = "fnr") fnr: String?,
        @RequestHeader headers: MultiValueMap<String, String>
    ): Bruker {
        val requestedPersonIdent = fnr ?: headers.getFirst(NAV_PERSONIDENT_HEADER)

        if (requestedPersonIdent.isNullOrEmpty()) {
            throw IllegalArgumentException("Did not find a PersonIdent in request headers or in Request param")
        } else {
            tilgangsKontroll.throwExceptionIfVeilederWithoutAccessOBO(requestedPersonIdent)

            val digitalKontaktinfo: DigitalKontaktinfo = dkifConsumer.kontaktinformasjon(requestedPersonIdent)
            val kontaktinfo = Kontaktinfo().copy(
                fnr = fnr,
                skalHaVarsel = digitalKontaktinfo.kanVarsles,
                epost = digitalKontaktinfo.epostadresse,
                tlf = digitalKontaktinfo.mobiltelefonnummer
            )
            return Bruker(
                navn = pdlConsumer.person(Fodselsnummer(requestedPersonIdent))?.fullName(),
                kontaktinfo = kontaktinfo,
                arbeidssituasjon = "ARBEIDSTAKER"
            )
        }
    }
}
