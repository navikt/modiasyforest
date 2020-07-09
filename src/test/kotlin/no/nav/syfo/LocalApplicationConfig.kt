package no.nav.syfo

import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import java.util.*

@Configuration
@Import(TokenGeneratorConfiguration::class)
class LocalApplicationConfig(environment: Environment) {
    init {
        System.setProperty("SECURITYTOKENSERVICE_URL", Objects.requireNonNull(environment.getProperty("securitytokenservice.url")))
        System.setProperty("SRVMODIASYFOREST_USERNAME", Objects.requireNonNull(environment.getProperty("srvmodiasyforest.username")))
        System.setProperty("SRVMODIASYFOREST_PASSWORD", Objects.requireNonNull(environment.getProperty("srvmodiasyforest.password")))
    }
}
