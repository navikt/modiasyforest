package no.nav.syfo.consumer.syfonarmesteleder

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.AzureAdTokenConsumer
import no.nav.syfo.metric.Metrikk
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class NarmesteLederConsumerTest {

    @MockBean
    private lateinit var azureAdTokenConsumer: AzureAdTokenConsumer

    @MockBean
    private lateinit var metrikk: Metrikk

    @Inject
    private lateinit var restTemplate: RestTemplate

    @Value("\${syfonarmesteleder.id}")
    private lateinit var syfonarmestelederId: String

    private lateinit var narmesteLederConsumer: NarmesteLederConsumer

    private lateinit var mockRestServiceServer: MockRestServiceServer

    @Before
    fun setup() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build()
        narmesteLederConsumer = NarmesteLederConsumer(azureAdTokenConsumer, metrikk, restTemplate, syfonarmestelederId)

        `when`(azureAdTokenConsumer.accessToken(syfonarmestelederId)).thenReturn("token")
    }

    @After
    fun tearDown() {
        mockRestServiceServer.verify()
    }

    @Test
    fun `get ledere for aktorID from syfonarmesteleder`() {
        val expectedLedereList: List<NarmesteLederRelasjon> = listOf(
                NarmesteLederRelasjon(
                        SYKMELDT_AKTOR_ID,
                        VIRKSOMHETSNUMMER,
                        LEDER_AKTOR_ID,
                        "narmesteLederTelefonnummer",
                        "narmesteLederEpost",
                        LocalDate.now(),
                        false,
                        false,
                        emptyList()
                )
        )
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(LEDERE_URL)).andRespond(withSuccess(ledereAsJsonObject(expectedLedereList), MediaType.APPLICATION_JSON))


        val actualLedere: List<NarmesteLederRelasjon> = narmesteLederConsumer.narmesteLederRelasjonerLedere(SYKMELDT_AKTOR_ID)

        assertThat(actualLedere.size).isEqualTo(expectedLedereList.size)
        assertThat(actualLedere[0].aktorId).isEqualTo(expectedLedereList[0].aktorId)
        assertThat(actualLedere[0].orgnummer).isEqualTo(expectedLedereList[0].orgnummer)
        verify(azureAdTokenConsumer).accessToken(syfonarmestelederId)
        verify(metrikk).countEvent("call_syfonarmesteleder_ledere_success")
    }

    @Test
    fun `get ledere accepts empty list as result`() {
        val expectedEmptyLedereList: List<NarmesteLederRelasjon> = emptyList()
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(LEDERE_URL)).andRespond(withSuccess(ledereAsJsonObject(expectedEmptyLedereList), MediaType.APPLICATION_JSON))

        val actualLedere: List<NarmesteLederRelasjon> = narmesteLederConsumer.narmesteLederRelasjonerLedere(SYKMELDT_AKTOR_ID)

        assertThat(actualLedere.size).isEqualTo(expectedEmptyLedereList.size)
        verify(azureAdTokenConsumer).accessToken(syfonarmestelederId)
        verify(metrikk).countEvent("call_syfonarmesteleder_ledere_success")
    }

    @Test(expected = RestClientResponseException::class)
    fun `get ledere catches RestClientResponseException and counts metric`() {
        val errorMessage = "Feilmelding"
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(LEDERE_URL)).andRespond(withServerError().body(errorMessage))

        try {
            narmesteLederConsumer.narmesteLederRelasjonerLedere(SYKMELDT_AKTOR_ID)
        } catch (e: Exception) {
            verify(metrikk).countEvent("call_syfonarmesteleder_ledere_fail")
            throw e
        }
    }

    fun ledereAsJsonObject(narmesteLedere: List<NarmesteLederRelasjon>): String {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        return try {
            objectMapper.writeValueAsString(narmesteLedere)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val SYKMELDT_AKTOR_ID = "1234567890987"
        private const val LEDER_AKTOR_ID = "7890987654321"
        private const val VIRKSOMHETSNUMMER = "1234"

        private const val LEDERE_URL = "http://syfonarmesteleder/syfonarmesteleder/sykmeldt/$SYKMELDT_AKTOR_ID/narmesteledere"
    }
}
