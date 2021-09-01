package no.nav.syfo.consumer.narmesteleder

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR
import no.nav.syfo.testhelper.UserConstants.LEDER_FNR
import no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER
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
import java.time.OffsetDateTime
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class NarmesteLederConsumerTest {

    @MockBean
    private lateinit var azureAdTokenConsumer: AzureAdV2TokenConsumer

    @MockBean
    private lateinit var metrikk: Metrikk

    @Inject
    private lateinit var restTemplate: RestTemplate

    @Value("\${narmesteleder.id}")
    private lateinit var narmestelederId: String

    @Value("\${narmesteleder.url}")
    private lateinit var narmestelederUrl: String

    private lateinit var narmesteLederConsumer: NarmesteLederConsumer

    private lateinit var mockRestServiceServer: MockRestServiceServer

    private lateinit var ledereUrl: String

    @Before
    fun setup() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build()
        narmesteLederConsumer = NarmesteLederConsumer(
            azureAdTokenConsumer,
            metrikk,
            restTemplate,
            narmestelederUrl,
            narmestelederId,
        )
        ledereUrl = narmesteLederConsumer.narmestelederListUrl
        `when`(azureAdTokenConsumer.getSystemToken(narmestelederId)).thenReturn("token")
    }

    @After
    fun tearDown() {
        mockRestServiceServer.verify()
    }

    @Test
    fun `get ledere for aktorID from syfonarmesteleder`() {
        val expectedLedereList = listOf(
            NarmesteLederRelasjonDTO(
                narmesteLederRelasjon = NarmesteLederDTO(
                    fnr = ARBEIDSTAKER_FNR,
                    orgnummer = VIRKSOMHETSNUMMER,
                    narmesteLederFnr = LEDER_FNR,
                    narmesteLederTelefonnummer = "narmesteLederTelefonnummer",
                    narmesteLederEpost = "narmesteLederEpost",
                    aktivFom = LocalDate.now(),
                    aktivTom = LocalDate.now(),
                    arbeidsgiverForskutterer = false,
                    tilganger = emptyList(),
                    timestamp = OffsetDateTime.now(),
                    navn = "Leder Ledersen",
                )
            )
        )
        mockRestServiceServer.expect(
            ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(ledereUrl)
        ).andRespond(withSuccess(ledereAsJsonObject(expectedLedereList), MediaType.APPLICATION_JSON))

        val actualLedere: List<NarmesteLederRelasjonDTO> = narmesteLederConsumer.narmesteLederRelasjonerLedere(Fodselsnummer(ARBEIDSTAKER_FNR))

        assertThat(actualLedere.size).isEqualTo(expectedLedereList.size)
        assertThat(actualLedere.first().narmesteLederRelasjon.fnr).isEqualTo(expectedLedereList.first().narmesteLederRelasjon.fnr)
        assertThat(actualLedere.first().narmesteLederRelasjon.orgnummer).isEqualTo(expectedLedereList.first().narmesteLederRelasjon.orgnummer)
        verify(azureAdTokenConsumer).getSystemToken(narmestelederId)
        verify(metrikk).countEvent("call_syfonarmesteleder_ledere_success")
    }

    @Test
    fun `get ledere accepts empty list as result`() {
        val expectedEmptyLedereList: List<NarmesteLederRelasjonDTO> = emptyList()
        mockRestServiceServer.expect(
            ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(ledereUrl),
        ).andRespond(withSuccess(ledereAsJsonObject(expectedEmptyLedereList), MediaType.APPLICATION_JSON))

        val actualLedere: List<NarmesteLederRelasjonDTO> = narmesteLederConsumer.narmesteLederRelasjonerLedere(Fodselsnummer(ARBEIDSTAKER_FNR))

        assertThat(actualLedere.size).isEqualTo(expectedEmptyLedereList.size)
        verify(azureAdTokenConsumer).getSystemToken(narmestelederId)
        verify(metrikk).countEvent("call_syfonarmesteleder_ledere_success")
    }

    @Test(expected = RestClientResponseException::class)
    fun `get ledere catches RestClientResponseException and counts metric`() {
        val errorMessage = "Feilmelding"
        mockRestServiceServer.expect(
            ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(ledereUrl),
        ).andRespond(withServerError().body(errorMessage))

        try {
            narmesteLederConsumer.narmesteLederRelasjonerLedere(Fodselsnummer(ARBEIDSTAKER_FNR))
        } catch (e: Exception) {
            verify(metrikk).countEvent("call_syfonarmesteleder_ledere_fail")
            throw e
        }
    }

    fun ledereAsJsonObject(narmesteLedere: List<NarmesteLederRelasjonDTO>): String {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        return try {
            objectMapper.writeValueAsString(narmesteLedere)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
