package no.nav.syfo.testhelper

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.consumer.pdl.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.util.bearerCredentials
import org.springframework.http.*
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators

fun mockAndExpectPdlIdenterRequest(mockRestServiceServer: MockRestServiceServer, pdlUrl: String) {
    val pdlIdenterResponse = PdlIdenterResponse(
        errors = emptyList(),
        data = PdlHentIdenter(
            hentIdenter = PdlIdenter(
                identer = listOf(
                    PdlIdent(
                        ident = ARBEIDSTAKER_FNR,
                        historisk = false,
                        gruppe = IdentType.FOLKEREGISTERIDENT.name
                    ),
                    PdlIdent(
                        ident = ARBEIDSTAKER_AKTORID,
                        historisk = false,
                        gruppe = IdentType.AKTORID.name
                    )
                )
            )
        )
    )

    val json = ObjectMapper().writeValueAsString(pdlIdenterResponse)

    mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(pdlUrl))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
        .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, bearerCredentials(UserConstants.STS_TOKEN)))
        .andRespond(MockRestResponseCreators.withSuccess(json, MediaType.APPLICATION_JSON))
}
