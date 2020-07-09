package no.nav.syfo.config.consumer

import no.nav.syfo.oidc.OIDCUtil.leggTilOnBehalfOfOutInterceptorForOIDC
import no.nav.syfo.services.ws.*
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.HentSykepengesoeknadListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.SykepengesoeknadV1
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeRequest
import no.nav.tjeneste.virksomhet.sykepengesoeknad.v1.meldinger.WSHentSykepengesoeknadListeResponse
import org.apache.cxf.frontend.ClientProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

@Configuration
class SykepengesoknadConfig {
    private lateinit var port: SykepengesoeknadV1

    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun sykepengesoeknadV1(@Value("\${sykepengesoeknad.v1.url}") serviceUrl: String): SykepengesoeknadV1 {
        val port = WsClient<SykepengesoeknadV1>().createPort(serviceUrl, SykepengesoeknadV1::class.java, listOf<Handler<*>>(LogErrorHandler()))
        STSClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port)
        this.port = port
        return port
    }

    @Throws(HentSykepengesoeknadListeSikkerhetsbegrensning::class)
    fun hentSykepengesoeknadListe(request: WSHentSykepengesoeknadListeRequest?, OIDCToken: String): WSHentSykepengesoeknadListeResponse {
        leggTilOnBehalfOfOutInterceptorForOIDC(ClientProxy.getClient(port), OIDCToken)
        return port.hentSykepengesoeknadListe(request)
    }

    companion object {
        const val MOCK_KEY = "sykepengesoknad.syfoservice.withmock"
    }
}