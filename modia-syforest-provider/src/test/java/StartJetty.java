import no.nav.brukerdialog.security.context.InternbrukerSubjectHandler;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.File;
import java.security.Security;

import static java.lang.System.setProperty;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setServicebruker;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setVeilederIdent;
import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


public class StartJetty {

    // Ta Edit Confiugration på StartJetty. Sett Provider-modulen som working directory

    public static void main(String[] args) {
        runJetty();
    }

    private static void runJetty() {
        setFrom("jetty-environment.properties");
        setupKeyAndTrustStore();

        setVeilederIdent("***REMOVED***");
        setServicebruker("srvmodiasyforest");
        setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", InternbrukerSubjectHandler.class.getName());
        setProperty("org.apache.geronimo.jaspic.configurationFile", "src/test/resources/jaspiconf.xml");
        Security.setProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY, AuthConfigFactoryImpl.class.getCanonicalName());

        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiasyforest")
                .port(8084)
                .configureForJaspic()
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
