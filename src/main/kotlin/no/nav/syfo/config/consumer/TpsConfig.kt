package no.nav.syfo.config.consumer

import no.nav.syfo.consumer.util.ws.*
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

@Configuration
class TpsConfig {
    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun brukerprofilV3(@Value("\${brukerprofil.v3.url}") serviceUrl: String): BrukerprofilV3 {
        val port = factory(serviceUrl)
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(serviceUrl: String): BrukerprofilV3 {
        return WsClient<BrukerprofilV3>()
            .createPort(serviceUrl, BrukerprofilV3::class.java, listOf<Handler<*>>(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "brukerprofilv3.withmock"
    }
}
