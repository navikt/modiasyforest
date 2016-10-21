import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.security.loginmodule.DummyRole;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import org.eclipse.jetty.jaas.JAASLoginService;

import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
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
        setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());

        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiasyforest")
                .port(8084)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .withLoginService(createLoginService())
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }


    public static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }
}
