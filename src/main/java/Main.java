import no.nav.sbl.dialogarena.modiasyforest.config.ApplicationConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.*;
import static no.nav.apiapp.ApiApp.startApp;

public class Main {
    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);
        setupMetricsProperties();
        setProperty("OIDC_REDIRECT_URL", getProperty("VEILARBLOGIN_REDIRECT_URL_URL"));
        startApp(ApplicationConfig.class, args);
    }

    private static void setupMetricsProperties() throws UnknownHostException {
        setProperty("applicationName", "modiasyforest");
        setProperty("node.hostname", InetAddress.getLocalHost().getHostName());
        setProperty("environment.name", getProperty("FASIT_ENVIRONMENT_NAME"));
    }
}