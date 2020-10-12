package no.nav.syfo.consumer.aktorregister

import no.nav.syfo.config.CacheConfig
import no.nav.syfo.consumer.aktorregister.domain.IdentinfoListe
import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.inject.Inject

@Service
class AktorregisterConsumer @Inject constructor(
    @Value("\${aktorregister.url}") private val baseUrl: String,
    private val metric: Metrikk,
    private val restTemplate: RestTemplate,
    private val stsConsumer: StsConsumer
) {
    private val responseType: ParameterizedTypeReference<Map<String, IdentinfoListe>> = object : ParameterizedTypeReference<Map<String, IdentinfoListe>>() {}

    fun aktorId(fnr: Fodselsnummer): AktorId {
        return AktorId(getAktorId(fnr))
    }

    fun fodselsnummer(aktorId: AktorId): Fodselsnummer {
        return Fodselsnummer(getFodselsnummer(aktorId))
    }

    @Cacheable(value = [CacheConfig.CACHENAME_AKTOR_ID], key = "#fnr.value", condition = "#fnr.value != null")
    fun getAktorId(fnr: Fodselsnummer): String {
        val response = getIdentFromAktorregister(fnr.value, IdentType.AktoerId)
        return currentIdentFromAktorregisterResponse(response, fnr.value, IdentType.AktoerId)
    }

    @Cacheable(value = [CacheConfig.CACHENAME_AKTOR_FNR], key = "#aktorId.value", condition = "#aktorId.value != null")
    fun getFodselsnummer(aktorId: AktorId): String {
        val response = getIdentFromAktorregister(aktorId.value, IdentType.NorskIdent)
        return currentIdentFromAktorregisterResponse(response, aktorId.value, IdentType.NorskIdent)
    }

    private fun getIdentFromAktorregister(ident: String, identType: IdentType): Map<String, IdentinfoListe> {
        val entity = entity(ident)
        val uriString = UriComponentsBuilder.fromHttpUrl("$baseUrl/identer").queryParam("gjeldende", true).toUriString()
        try {
            val response = restTemplate.exchange(
                uriString,
                HttpMethod.GET,
                entity,
                responseType
            )
            val responseBody = response.body!!
            metric.countEvent("call_aktorregister_success")
            return responseBody
        } catch (e: RestClientResponseException) {
            metric.countEvent("call_aktorregister_fail")
            val message = "Call to get Ident from Aktorregister failed with status HTTP-${e.rawStatusCode} for IdentType=${identType.name}"
            LOG.error(message)
            throw e
        }
    }

    private fun entity(ident: String): HttpEntity<String> {
        val stsToken = stsConsumer.token()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers[HttpHeaders.AUTHORIZATION] = bearerCredentials(stsToken)
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_PERSONIDENTER_HEADER] = ident
        return HttpEntity(headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AktorregisterConsumer::class.java)
    }
}

enum class IdentType {
    AktoerId, NorskIdent
}
