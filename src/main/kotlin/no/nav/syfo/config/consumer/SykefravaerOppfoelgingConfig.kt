package no.nav.syfo.config.consumer

import no.nav.syfo.services.ws.*
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

@Configuration
class SykefravaerOppfoelgingConfig {
    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun sykefravaersoppfoelgingV1(@Value("\${sykefravaersoppfoelging.v1.url}") serviceUrl: String): SykefravaersoppfoelgingV1 {
        val port = factory(serviceUrl)
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(serviceUrl: String): SykefravaersoppfoelgingV1 {
        return WsClient<SykefravaersoppfoelgingV1>()
            .createPort(serviceUrl, SykefravaersoppfoelgingV1::class.java, listOf<Handler<*>>(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "oppfoelging.syfoservice.withmock"
    }
}
