import no.nav.apiapp.ApiApp;
import no.nav.sbl.dialogarena.modiasyforest.config.ApplicationConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.*;
import static no.nav.brukerdialog.security.Constants.OIDC_REDIRECT_URL_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.modiasyforest.config.ApplicationConfig.VEILARBLOGIN_REDIRECT_URL_URL;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {
    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);
        setupMetricsProperties();

        setProperty(OIDC_REDIRECT_URL_PROPERTY_NAME, getRequiredProperty(VEILARBLOGIN_REDIRECT_URL_URL));

        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void setupMetricsProperties() throws UnknownHostException {
        setProperty("applicationName", "modiasyforest");
        setProperty("node.hostname", InetAddress.getLocalHost().getHostName());
        setProperty("environment.name", getProperty("FASIT_ENVIRONMENT_NAME"));
    }
}