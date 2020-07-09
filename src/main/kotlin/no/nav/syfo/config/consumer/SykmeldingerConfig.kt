package no.nav.syfo.config.consumer

import no.nav.syfo.consumer.util.ws.*
import no.nav.syfo.oidc.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC
import no.nav.tjeneste.virksomhet.sykmelding.v1.HentOppfoelgingstilfelleListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykmelding.v1.SykmeldingV1
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeRequest
import no.nav.tjeneste.virksomhet.sykmelding.v1.meldinger.WSHentOppfoelgingstilfelleListeResponse
import org.apache.cxf.frontend.ClientProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

@Configuration
class SykmeldingerConfig {
    private lateinit var port: SykmeldingV1

    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun SykmeldingV1(@Value("\${sykmelding.v1.url}") serviceUrl: String): SykmeldingV1 {
        val port = WsClient<SykmeldingV1>().createPort(serviceUrl, SykmeldingV1::class.java, listOf<Handler<*>>(LogErrorHandler()))
        STSClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port)
        this.port = port
        return port
    }

    @Throws(HentOppfoelgingstilfelleListeSikkerhetsbegrensning::class)
    fun hentOppfoelgingstilfelleListe(request: WSHentOppfoelgingstilfelleListeRequest, OIDCToken: String): WSHentOppfoelgingstilfelleListeResponse {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken)
        return port.hentOppfoelgingstilfelleListe(request)
    }

    companion object {
        const val MOCK_KEY = "sykmelding.syfoservice.withmock"
    }
}
