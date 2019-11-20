package no.nav.syfo.consumer.veilederoppgaver

import no.nav.syfo.util.basicCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl
import java.util.Collections.singletonMap

@Service
class VeilederoppgaverConsumer(
        @Value("\${syfoveilederoppgaver.system.v1.url}") private val syfoveilederoppgaverUrl: String,
        @Value("\${syfoveilederoppgaver.systemapi.username}") private val syfoveilederoppgaverUsername: String,
        @Value("\${syfoveilederoppgaver.systemapi.password}") private val syfoveilederoppgaverPassword: String,
        private val template: RestTemplate
) {
    private val getVeilederoppgaverUriTemplate: UriComponentsBuilder
    private val updateVeilederoppgaverUriTemplate: UriComponentsBuilder

    init {
        getVeilederoppgaverUriTemplate = fromHttpUrl(syfoveilederoppgaverUrl)
                .queryParam(FNR, FNR_PLACEHOLDER)
        updateVeilederoppgaverUriTemplate = fromHttpUrl(syfoveilederoppgaverUrl)
                .path("/actions")
    }

    fun getVeilederoppgaver(fnr: String): List<Veilederoppgave> {
        val request = HttpEntity<Any>(authorizationHeader())

        val getVeilederoppgaverForFnrUri = getVeilederoppgaverUriTemplate.build(singletonMap<String, String>(FNR, fnr))

        val response = template.exchange<List<Veilederoppgave>>(
                getVeilederoppgaverForFnrUri,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<List<Veilederoppgave>>() {}
        )
        return response.body ?: emptyList()
    }

    fun updateVeilederoppgave(updateVeilederoppgave: UpdateVeilederoppgave): Long? {
        val request: HttpEntity<UpdateVeilederoppgave> = HttpEntity(updateVeilederoppgave, authorizationHeader())

        val updateVeilederoppgaveUriWith = updateVeilederoppgaverUriTemplate
                .build()
                .toUri()

        val response = template.exchange<Long>(
                updateVeilederoppgaveUriWith,
                HttpMethod.PUT,
                request,
                object : ParameterizedTypeReference<Long>() {}
        )
        return response.body
    }

    private fun authorizationHeader(): HttpHeaders {
        val credentials = basicCredentials(syfoveilederoppgaverUsername, syfoveilederoppgaverPassword)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, credentials)
        return headers
    }

    companion object {

        const val FNR = "fnr"
        const val FNR_PLACEHOLDER = "{$FNR}"
    }
}
