package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.config.CacheConfig.Companion.CACHENAME_NARMESTELEDER_LEDERE
import no.nav.syfo.consumer.azuread.AzureAdTokenConsumer
import no.nav.syfo.domain.AktorId
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.*
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
    @Value("\${syfonarmesteleder.url}") private val syfonarmestelederUrl: String,
    @Value("\${syfonarmesteleder.id}") private val syfonarmestelederId: String
) {
    private val syfonarmestelederBaseUrl: String

    init {
        syfonarmestelederBaseUrl = "$syfonarmestelederUrl$SYFONARMESTELEDER_BASE_PATH"
    }

    @Cacheable(value = [CACHENAME_NARMESTELEDER_LEDERE], key = "#aktorId.value", condition = "#aktorId.value != null")
    fun narmesteLederRelasjonerLedere(aktorId: AktorId): List<NarmesteLederRelasjon> {
        try {
            val response = restTemplate.exchange(
                getLedereUrl(aktorId),
                HttpMethod.GET,
                entity(),
                object : ParameterizedTypeReference<List<NarmesteLederRelasjon>>() {}
            )
            metrikk.countEvent(CALL_SYFONARMESTELEDER_LEDERE_SUCCESS)

            return response.body ?: run {
                LOG.error("Request to get Ledere from Syfonarmesteleder was null. Response: $response")
                emptyList()
            }
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

    private fun getLedereUrl(aktorId: AktorId): String {
        return UriComponentsBuilder
            .fromHttpUrl("$syfonarmestelederBaseUrl/sykmeldt/${aktorId.value}/narmesteledere")
            .toUriString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NarmesteLederConsumer::class.java)
        private const val SYFONARMESTELEDER_BASE_PATH = "/syfonarmesteleder"

        private const val CALL_SYFONARMESTELEDER_LEDERE_BASE = "call_syfonarmesteleder_ledere"
        private const val CALL_SYFONARMESTELEDER_LEDERE_FAIL = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_fail"
        private const val CALL_SYFONARMESTELEDER_LEDERE_SUCCESS = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_success"
    }
}
