package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.selftest.TeksterInvalideringServlet;
import no.nav.sbl.tekster.TeksterAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;

@Configuration
public class TeksterConfig {

    private String ledeteksterPath = getProperty("folder.ledetekster.path");

    @Bean(name = "moteTekster")
    public TeksterAPI moteTekster() {
        return new TeksterAPI(ledeteksterPath + "/tekster", "mote");
    }

    @Bean(name = "syfoTekster")
    public TeksterAPI sykefravaerTekster() {
        return new TeksterAPI(ledeteksterPath + "/tekster", "sykefravaer");
    }

    @Bean(name = "commonTekster")
    public TeksterAPI commonTekster() {
        return new TeksterAPI(ledeteksterPath + "/tekster", "common");
    }

    @Bean
    public TeksterInvalideringServlet teksterInvalideringServlet() {
        return new TeksterInvalideringServlet();
    }
}
