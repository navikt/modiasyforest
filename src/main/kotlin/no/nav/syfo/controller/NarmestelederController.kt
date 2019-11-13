package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.controller.domain.NaermesteLeder
import no.nav.syfo.controller.domain.sykmelding.Sykmelding
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.*
import no.nav.syfo.utils.Metrikk
import no.nav.tjeneste.virksomhet.sykmelding.v1.informasjon.WSSkjermes
import org.springframework.web.bind.annotation.*
import java.util.Collections.emptyList
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/naermesteleder"])
class NarmestelederController @Inject
constructor(
        private val metrikk: Metrikk,
        private val naermesteLederService: NaermesteLederService,
        private val sykmeldingService: SykmeldingService,
        private val tilgangService: TilgangService
) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getNarmesteledere(@RequestParam(value = "fnr") fnr: String): List<NaermesteLeder> {
        metrikk.tellEndepunktKall("hent_naermesteledere")

        tilgangService.throwExceptionIfVeilederWithoutAccess(fnr)

        val sykmeldinger: List<Sykmelding> = try {
            sykmeldingService.hentSykmeldinger(fnr, listOf(WSSkjermes.SKJERMES_FOR_ARBEIDSGIVER), AZURE)
        } catch (e: Exception) {
            emptyList()
        }

        val naermesteledere = naermesteLederService.hentNaermesteledere(fnr)
        naermesteledere.addAll(naermesteLederService.hentOrganisasjonerSomIkkeHarSvart(naermesteledere, sykmeldinger))
        var idcounter: Long = 0
        for (naermesteleder in naermesteledere) {
            naermesteleder.id = idcounter++
        }
        return naermesteledere
    }
}
