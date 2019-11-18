package no.nav.syfo.consumer.veilederoppgaver

import no.nav.syfo.util.basicCredentials
import org.slf4j.LoggerFactory
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

    init {
        getVeilederoppgaverUriTemplate = fromHttpUrl(syfoveilederoppgaverUrl)
                .queryParam(FNR, FNR_PLACEHOLDER)
    }

    fun getVeilederoppgaver(fnr: String): List<Veilederoppgave> {
        val credentials = basicCredentials(syfoveilederoppgaverUsername, syfoveilederoppgaverPassword)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, credentials)
        val request = HttpEntity<Any>(headers)

        val acccessToPersonUriWithFnr = getVeilederoppgaverUriTemplate.build(singletonMap<String, String>(FNR, fnr))

        LOG.info("JTRACE: call syfoveilederoppgaver with url {}", acccessToPersonUriWithFnr)
        LOG.info("JTRACE: call syfoveilederoppgaver with cred cred {} and {}", syfoveilederoppgaverUsername.length.toString(), syfoveilederoppgaverPassword.length.toString())

        val response = template.exchange<List<Veilederoppgave>>(
                acccessToPersonUriWithFnr,
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<List<Veilederoppgave>>() {}
        )
        return response.body ?: emptyList()
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(VeilederoppgaverConsumer::class.java)
        const val FNR = "fnr"
        const val FNR_PLACEHOLDER = "{$FNR}"
    }
}
