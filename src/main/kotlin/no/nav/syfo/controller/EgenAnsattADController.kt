package no.nav.syfo.controller

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims
import no.nav.syfo.services.EgenAnsattService
import org.springframework.web.bind.annotation.*

import javax.inject.Inject

import javax.ws.rs.core.MediaType.APPLICATION_JSON
import no.nav.syfo.oidc.OIDCIssuer.AZURE

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = ["/api/internad/egenansatt/{fnr}"])
class EgenAnsattADController @Inject
constructor(private val egenAnsattService: EgenAnsattService) {

    @GetMapping(produces = [APPLICATION_JSON])
    fun getIsEgenAnsatt(@PathVariable("fnr") fnr: String): EgenAnsattSvar {
        return EgenAnsattSvar(egenAnsattService.erEgenAnsatt(fnr))
    }

    inner class EgenAnsattSvar(var erEgenAnsatt: Boolean)
}
