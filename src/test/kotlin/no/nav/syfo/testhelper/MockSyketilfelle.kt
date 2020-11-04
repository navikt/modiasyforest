package no.nav.syfo.testhelper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.syfo.consumer.syketilfelle.domain.KOppfolgingstilfelle
import no.nav.syfo.consumer.syketilfelle.domain.KSyketilfelledag
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.util.bearerCredentials
import org.springframework.http.*
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import java.time.LocalDate
import java.time.LocalDateTime

fun mockAndExpectSyketilfelleRequest(mockRestServiceServer: MockRestServiceServer, syketilfelleUrl: String) {
    val syketilfelleResponse = generateOppfolgingstilfelle()
    val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(KotlinModule())
    val json = objectMapper
        .writeValueAsString(syketilfelleResponse)

    mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(syketilfelleUrl))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, bearerCredentials(UserConstants.STS_TOKEN)))
        .andRespond(MockRestResponseCreators.withSuccess(json, MediaType.APPLICATION_JSON))
}

val OPPFOLGINGSTILFELLE_PERIODE_FOM = LocalDate.now().minusDays(10)
val OPPFOLGINGSTILFELLE_PERIODE_TOM = LocalDate.now().plusDays(10)

fun generateOppfolgingstilfelle() =
    KOppfolgingstilfelle(
        ARBEIDSTAKER_AKTORID,
        UserConstants.VIRKSOMHETSNUMMER,
        listOf(
            KSyketilfelledag(
                OPPFOLGINGSTILFELLE_PERIODE_FOM,
                null
            ),
            KSyketilfelledag(
                OPPFOLGINGSTILFELLE_PERIODE_TOM,
                null
            )
        ),
        KSyketilfelledag(
            LocalDate.now().minusDays(1),
            null
        ),
        0,
        false,
        LocalDateTime.now()
    )
