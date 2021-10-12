package no.nav.syfo.consumer.pdl

import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class PdlConsumer(
    private val metric: Metrikk,
    @Value("\${pdl.url}") private val pdlUrl: String,
    private val stsConsumer: StsConsumer,
    private val restTemplate: RestTemplate
) {
    fun aktorId(fodselsnummer: Fodselsnummer, callId: String): AktorId {
        return identer(fodselsnummer.value, callId).aktorId()
            ?: throw PdlRequestFailedException("Request to get Ident of Type ${IdentType.AKTORID.name} from PDL Failed")
    }

    fun identer(ident: String, callId: String): PdlHentIdenter? {
        val request = PdlHentIdenterRequest(
            query = getPdlQuery("/pdl/hentIdenter.graphql"),
            variables = PdlHentIdenterRequestVariables(
                ident = ident,
                historikk = false,
                grupper = listOf(
                    IdentType.AKTORID.name,
                    IdentType.FOLKEREGISTERIDENT.name
                )
            )
        )
        val entity = HttpEntity(
            request,
            createRequestHeaders()
        )
        try {
            val pdlReponseEntity = restTemplate.exchange(
                pdlUrl,
                HttpMethod.POST,
                entity,
                PdlIdenterResponse::class.java
            )
            val pdlIdenterReponse = pdlReponseEntity.body!!
            return if (pdlIdenterReponse.errors != null && pdlIdenterReponse.errors.isNotEmpty()) {
                metric.countEvent(CALL_PDL_IDENTER_FAIL)
                pdlIdenterReponse.errors.forEach {
                    LOG.error("Error while requesting Identer from PersonDataLosningen: ${it.errorMessage()}")
                }
                null
            } else {
                metric.countEvent(CALL_PDL_IDENTER_SUCCESS)
                pdlIdenterReponse.data
            }
        } catch (exception: RestClientResponseException) {
            metric.countEvent(CALL_PDL_IDENTER_FAIL)
            LOG.error("Error from PDL with request-url: $pdlUrl", exception)
            throw exception
        }
    }

    fun person(fnr: Fodselsnummer): PdlHentPerson? {
        val request = PdlRequest(
            query = getPdlQuery("/pdl/hentPerson.graphql"),
            variables = Variables(fnr.value)
        )
        val entity = HttpEntity(
            request,
            createRequestHeaders()
        )
        try {
            val pdlPerson = restTemplate.exchange(
                pdlUrl,
                HttpMethod.POST,
                entity,
                PdlPersonResponse::class.java
            )

            val pdlPersonReponse = pdlPerson.body!!
            return if (pdlPersonReponse.errors != null && pdlPersonReponse.errors.isNotEmpty()) {
                metric.countEvent(CALL_PDL_FAIL)
                pdlPersonReponse.errors.forEach {
                    LOG.error("Error while requesting person from PersonDataLosningen: ${it.errorMessage()}")
                }
                null
            } else {
                metric.countEvent(CALL_PDL_SUCCESS)
                pdlPersonReponse.data
            }
        } catch (exception: RestClientResponseException) {
            metric.countEvent(CALL_PDL_FAIL)
            LOG.error("Error from PDL with request-url: $pdlUrl", exception)
            throw exception
        }
    }

    private fun getPdlQuery(queryFilePath: String): String {
        return this::class.java.getResource(queryFilePath)
            .readText()
            .replace("[\n\r]", "")
    }

    private fun createRequestHeaders(): HttpHeaders {
        val stsToken: String = stsConsumer.token()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
        headers.set(AUTHORIZATION, bearerCredentials(stsToken))
        headers.set(NAV_CONSUMER_TOKEN_HEADER, bearerCredentials(stsToken))
        return headers
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PdlConsumer::class.java)

        private const val CALL_PDL_BASE = "call_pdl"
        const val CALL_PDL_FAIL = "${CALL_PDL_BASE}_fail"
        const val CALL_PDL_SUCCESS = "${CALL_PDL_BASE}_success"
        const val CALL_PDL_IDENTER_FAIL = "${CALL_PDL_BASE}_identer_fail"
        const val CALL_PDL_IDENTER_SUCCESS = "${CALL_PDL_BASE}_identer_success"
    }
}
