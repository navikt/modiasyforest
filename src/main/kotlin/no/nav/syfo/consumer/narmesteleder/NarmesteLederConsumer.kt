package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.config.CacheConfig.Companion.CACHENAME_NARMESTELEDER_LEDERE
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.domain.Fodselsnummer
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

@Component
class NarmesteLederConsumer @Autowired constructor(
    private val azureAdTokenConsumer: AzureAdV2TokenConsumer,
    private val metrikk: Metrikk,
    private val restTemplate: RestTemplate,
    @Value("\${narmesteleder.url}") private val narmestelederUrl: String,
    @Value("\${narmesteleder.id}") private val narmestelederClientId: String
) {
    val narmestelederListUrl: String

    init {
        narmestelederListUrl = "$narmestelederUrl$NARMESTELEDERE_PATH"
    }

    @Cacheable(value = [CACHENAME_NARMESTELEDER_LEDERE], key = "#personIdentNumber.value", condition = "#personIdentNumber.value != null")
    fun narmesteLederRelasjonerLedere(personIdentNumber: Fodselsnummer): List<NarmesteLederRelasjonDTO> {
        try {
            val response = restTemplate.exchange(
                narmestelederListUrl,
                HttpMethod.GET,
                entity(personIdentNumber = personIdentNumber),
                object : ParameterizedTypeReference<List<NarmesteLederRelasjonDTO>>() {}
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

    private fun entity(personIdentNumber: Fodselsnummer): HttpEntity<*> {
        val token = azureAdTokenConsumer.getSystemToken(scopeClientId = narmestelederClientId)
        val headers = HttpHeaders()
        headers[HttpHeaders.AUTHORIZATION] = bearerCredentials(token)
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        headers[SYKMELDT_FNR_HEADER] = personIdentNumber.value
        return HttpEntity<Any>(headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NarmesteLederConsumer::class.java)

        const val NARMESTELEDERE_PATH = "/sykmeldt/narmesteledere?utvidet=ja"
        const val SYKMELDT_FNR_HEADER = "Sykmeldt-Fnr"

        private const val CALL_SYFONARMESTELEDER_LEDERE_BASE = "call_syfonarmesteleder_ledere"
        private const val CALL_SYFONARMESTELEDER_LEDERE_FAIL = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_fail"
        private const val CALL_SYFONARMESTELEDER_LEDERE_SUCCESS = "${CALL_SYFONARMESTELEDER_LEDERE_BASE}_success"
    }
}
