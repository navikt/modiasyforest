package no.nav.syfo.localconfig;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

@Configuration
@Import(TokenGeneratorConfiguration.class)
public class LocalApplicationConfig {

    public LocalApplicationConfig(Environment environment) {
        System.setProperty("SECURITYTOKENSERVICE_URL", requireNonNull(environment.getProperty("securitytokenservice.url")));
        System.setProperty("SRVMODIASYFOREST_USERNAME", requireNonNull(environment.getProperty("srvmodiasyforest.username")));
        System.setProperty("SRVMODIASYFOREST_PASSWORD", requireNonNull(environment.getProperty("srvmodiasyforest.password")));
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestInterceptor... interceptors) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(asList(interceptors));
        return template;
    }
}
