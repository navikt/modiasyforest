package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.consumer.AktorConsumer
import no.nav.syfo.controller.domain.sykepengesoknad.Sykepengesoknad
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.SykepengesoknaderService
import no.nav.syfo.services.TilgangService
import no.nav.syfo.utils.Metrikk
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/sykepengesoknader"])
class SykepengesoknadADController @Inject
constructor(
        private val metrikk: Metrikk,
        private val aktorConsumer: AktorConsumer,
        private val sykepengesoknaderService: SykepengesoknaderService,
        private val tilgangService: TilgangService
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getSykepengesoknader(@RequestParam(value = "fnr") fnr: String): List<Sykepengesoknad> {
        metrikk.tellEndepunktKall("hent_sykepengesoknader")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)

        try {
            return sykepengesoknaderService.hentSykepengesoknader(aktorConsumer.hentAktoerIdForFnr(fnr), AZURE)
        } catch (e: ForbiddenException) {
            metrikk.tellHentSykepengesoknader403()
            throw e
        }
    }
}
