package no.nav.syfo.testhelper

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.consumer.aktorregister.IdentType
import no.nav.syfo.consumer.aktorregister.domain.Identinfo
import no.nav.syfo.consumer.aktorregister.domain.IdentinfoListe
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.util.bearerCredentials
import org.springframework.http.*
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.util.UriComponentsBuilder

private const val MOCK_AKTORID_PREFIX = "10"

fun mockAktorId(fnr: String): String {
    return "$MOCK_AKTORID_PREFIX$fnr"
}

fun mockAndExpectAktorregRequest(mockRestServiceServer: MockRestServiceServer, baseUrl: String) {
    val uriString = UriComponentsBuilder.fromHttpUrl("$baseUrl/identer")
        .queryParam("gjeldende", true)
        .toUriString()

    val identMap = mapOf(ARBEIDSTAKER_FNR to IdentinfoListe(
        listOf(
            Identinfo(
                ident = ARBEIDSTAKER_FNR,
                identgruppe = IdentType.NorskIdent.name,
                gjeldende = true
            ),
            Identinfo(
                ident = UserConstants.ARBEIDSTAKER_AKTORID,
                identgruppe = IdentType.AktoerId.name,
                gjeldende = true
            )
        ),
        feilmelding = null
    ))

    val json = ObjectMapper().writeValueAsString(identMap)

    mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(uriString))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, bearerCredentials(UserConstants.STS_TOKEN)))
        .andRespond(MockRestResponseCreators.withSuccess(json, MediaType.APPLICATION_JSON))
}
