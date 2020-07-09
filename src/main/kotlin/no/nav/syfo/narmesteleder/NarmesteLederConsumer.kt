package no.nav.syfo.narmesteleder

import no.nav.syfo.azuread.AzureAdTokenConsumer
import no.nav.syfo.config.CacheConfig.Companion.CACHENAME_NARMESTELEDER_LEDERE
import no.nav.syfo.util.*
import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class NarmesteLederConsumer @Autowired constructor(
    private val azureAdTokenConsumer: AzureAdTokenConsumer,
    private val metrikk: Metrikk,
    private val restTemplate: RestTemplate,
    @Value("\${syfonarmesteleder.id}") private val syfonarmestelederId: String
) {
    @Cacheable(value = [CACHENAME_NARMESTELEDER_LEDERE], key = "#aktorId", condition = "#aktorId != null")
    fun narmesteLederRelasjonerLedere(aktorId: String): List<NarmesteLederRelasjon> {
        try {
            val response = restTemplate.exchange(
                    getLedereUrl(aktorId),
                    HttpMethod.GET,
                    entity(),
                    object : ParameterizedTypeReference<List<NarmesteLederRelasjon>>() {}
            )
            metrikk.countEvent(CALL_SYFONARMESTELEDER_LEDERE_SUCCESS)

            return response.body!!
        } catch (e: RestClientResponseException) {
            LOG.error("Request to get Ledere from Syfonarmesteleder failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            metrikk.countEvent(CALL_SYFONARMESTELEDER_LEDERE_FAIL)
            throw e
        }
    }

    private fun entity(): HttpEntity<*> {
        val token = azureAdTokenConsumer.accessToken(syfonarmestelederId)
        val headers = HttpHeaders()
        headers[HttpHeaders.AUTHORIZATION] = bearerCredentials(token)
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        return HttpEntity<Any>(headers)
    }

    private fun getLedereUrl(aktorId: String): String {
        return UriComponentsBuilder
                .fromHttpUrl("$SYFONARMESTELEDER_BASEURL/sykmeldt/$aktorId/narmesteledere")
                .toUriString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NarmesteLederConsumer::class.java)
        private const val SYFONARMESTELEDER_BASEURL = "http://syfonarmesteleder/syfonarmesteleder"

        private const val CALL_SYFONARMESTELEDER_LEDERE_BASE = "call_syfonarmesteleder_ledere"
        private const val CALL_SYFONARMESTELEDER_LEDERE_FAIL = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_fail"
        private const val CALL_SYFONARMESTELEDER_LEDERE_SUCCESS = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_success"
    }
}
