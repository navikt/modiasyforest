package no.nav.sbl.dialogarena.modiasyforest;

import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@SpringBootApplication
@EnableCaching
@EnableOIDCTokenValidation(ignore = "org.springframework")
public class Application {
    public static void main(String[] args) {
        System.setProperty("no.nav.modig.security.sts.url", getRequiredProperty("SECURITYTOKENSERVICE_URL"));
        System.setProperty("no.nav.modig.security.systemuser.username", getRequiredProperty("SRVMODIASYFOREST_USERNAME"));
        System.setProperty("no.nav.modig.security.systemuser.password", getRequiredProperty("SRVMODIASYFOREST_PASSWORD"));

        SpringApplication.run(Application.class, args);
    }
}
