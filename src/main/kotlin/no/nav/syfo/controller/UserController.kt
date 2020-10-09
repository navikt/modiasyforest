package no.nav.syfo.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.consumer.BrukerprofilConsumer
import no.nav.syfo.consumer.TilgangConsumer
import no.nav.syfo.controller.domain.Bruker
import no.nav.syfo.controller.domain.Kontaktinfo
import no.nav.syfo.dkif.DigitalKontaktinfo
import no.nav.syfo.dkif.DkifConsumer
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/brukerinfo"])
class UserController @Inject constructor(
    private val brukerprofilConsumer: BrukerprofilConsumer,
    private val dkifConsumer: DkifConsumer,
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
        return brukerprofilConsumer.hentBruker(fnr)
            .copy(
                kontaktinfo = kontaktinfo,
                arbeidssituasjon = "ARBEIDSTAKER"
            )
    }
}
