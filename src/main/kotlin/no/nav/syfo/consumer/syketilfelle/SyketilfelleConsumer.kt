package no.nav.syfo.consumer.syketilfelle

import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.consumer.syketilfelle.domain.KOppfolgingstilfelle
import no.nav.syfo.domain.AktorId
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Component
class SyketilfelleConsumer(
    @Value("\${syfosyketilfelle.url}") private val syketilfelleUrl: String,
    private val metric: Metrikk,
    private val stsConsumer: StsConsumer,
    private val restTemplate: RestTemplate
) {
    fun getOppfolgingstilfelle(
        aktorId: AktorId,
        virksomhetsnummer: String,
        callId: String
    ): KOppfolgingstilfelle? {
        try {
            val response = restTemplate.exchange(
                getSyfosyketilfelleUrl(aktorId, virksomhetsnummer),
                HttpMethod.GET,
                entity(),
                KOppfolgingstilfelle::class.java
            )
            if (response.statusCodeValue == 204) {
                LOG.info("Syketilfelle returned HTTP-${response.statusCodeValue}: No Oppfolgingstilfelle was found for AktorId")
                return null
            }
            val responseBody: KOppfolgingstilfelle = response.body!!
            metric.countEvent(CALL_SYFOSYKETILFELLE_PERSON_SUCCESS)
            return responseBody
        } catch (e: RestClientResponseException) {
            LOG.error("Request to get Oppfolgingstilfelle for Person from Syfosyketilfelle failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            metric.countEvent(CALL_SYFOSYKETILFELLE_PERSON_FAIL)
            throw e
        }
    }

    private fun entity(): HttpEntity<*> {
        val token = stsConsumer.token()
        val headers = HttpHeaders()
        headers[HttpHeaders.AUTHORIZATION] = bearerCredentials(token)
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        return HttpEntity<Any>(headers)
    }

    private fun getSyfosyketilfelleUrl(
        aktorId: AktorId,
        virksomhetsnummer: String
    ): String {
        return "$syketilfelleUrl/kafka/oppfolgingstilfelle/beregn/${aktorId.value}/$virksomhetsnummer"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SyketilfelleConsumer::class.java)

        private const val CALL_SYFOSYKETILFELLE_PERSON_BASE = "call_syfosyketilfelle_person"
        private const val CALL_SYFOSYKETILFELLE_PERSON_FAIL = "${CALL_SYFOSYKETILFELLE_PERSON_BASE}_fail"
        private const val CALL_SYFOSYKETILFELLE_PERSON_SUCCESS = "${CALL_SYFOSYKETILFELLE_PERSON_BASE}_success"
    }
}
