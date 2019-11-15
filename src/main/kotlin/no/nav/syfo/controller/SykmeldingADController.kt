package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.SykmeldingService
import no.nav.syfo.services.TilgangService
import no.nav.syfo.utils.Metrikk
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/sykmeldinger"])
class SykmeldingADController @Inject
constructor(
        private val metrikk: Metrikk,
        private val sykmeldingService: SykmeldingService,
        private val tilgangService: TilgangService
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getSykmeldinger(
            @RequestParam(value = "fnr") fnr: String,
            @RequestParam(value = "type", required = false) type: String
    ): List<Sykmelding> {
        metrikk.tellEndepunktKall("hent_sykmeldinger")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)

        val filter = ArrayList<WSSkjermes>()
        if ("arbeidsgiver" == type) {
            filter.add(SKJERMES_FOR_ARBEIDSGIVER)
        }

        return sykmeldingService.hentSykmeldinger(fnr, filter, AZURE)
    }
}
