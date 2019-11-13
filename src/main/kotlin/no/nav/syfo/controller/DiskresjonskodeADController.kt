package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.oidc.OIDCIssuer.AZURE
import no.nav.syfo.services.DiskresjonskodeService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/diskresjonskode/{fnr}"])
class DiskresjonskodeADController @Inject
constructor(
        private var diskresjonskodeService: DiskresjonskodeService
) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getDiskresjonskode(@PathVariable fnr: String): DiskresjonskodeSvar {
        return DiskresjonskodeSvar(diskresjonskodeService.diskresjonskode(fnr))
    }

    inner class DiskresjonskodeSvar(var diskresjonskode: String)
}
