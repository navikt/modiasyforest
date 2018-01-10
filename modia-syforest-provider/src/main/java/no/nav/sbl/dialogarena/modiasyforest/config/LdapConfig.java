package no.nav.sbl.dialogarena.modiasyforest.config;

import no.nav.sbl.dialogarena.modiasyforest.services.LdapService;
import no.nav.sbl.dialogarena.types.Pingable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class LdapConfig {

    private static final String ENDEPUNKT_URL = getProperty("ldap.url");
    private static final String ENDEPUNKT_NAVN = "LDAP";
    private static final boolean KRITISK = true;


    @Bean
    public LdapService ldapService() {
        return new LdapService();
    }

    @Bean
    public Pingable ldapPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        return () -> {
            try {
                ldapService().ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

}
