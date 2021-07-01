package no.nav.syfo.controller.user.v1

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.AZURE
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.consumer.dkif.DigitalKontaktinfo
import no.nav.syfo.consumer.dkif.DkifConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.fullName
import no.nav.syfo.controller.user.domain.Bruker
import no.nav.syfo.controller.user.domain.Kontaktinfo
import no.nav.syfo.domain.Fodselsnummer
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/brukerinfo"])
class UserController @Inject constructor(
    private val dkifConsumer: DkifConsumer,
    private val pdlConsumer: PdlConsumer,
    private val tilgangsKontroll: TilgangConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUser(@RequestParam(value = "fnr") fnr: String): Bruker {
        tilgangsKontroll.throwExceptionIfVeilederWithoutAccess(fnr)

        val digitalKontaktinfo: DigitalKontaktinfo = dkifConsumer.kontaktinformasjon(fnr)
        val kontaktinfo = Kontaktinfo().copy(
            fnr = fnr,
            skalHaVarsel = digitalKontaktinfo.kanVarsles,
            epost = digitalKontaktinfo.epostadresse,
            tlf = digitalKontaktinfo.mobiltelefonnummer
        )
        return Bruker(
            navn = pdlConsumer.person(Fodselsnummer(fnr))?.fullName(),
            kontaktinfo = kontaktinfo,
            arbeidssituasjon = "ARBEIDSTAKER"
        )
    }
}
