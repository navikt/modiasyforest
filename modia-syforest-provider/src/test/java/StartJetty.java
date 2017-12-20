import no.nav.brukerdialog.security.context.InternbrukerSubjectHandler;
import no.nav.brukerdialog.security.domain.OidcCredential;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;
import java.io.IOException;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setOidcCredential;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setVeilederIdent;
import static no.nav.brukerdialog.tools.ISSOProvider.getIDToken;
import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


public class StartJetty {

    public static void main(String[] args) throws IOException {
        setFrom("jetty-environment.properties");
        setupKeyAndTrustStore();
        setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", InternbrukerSubjectHandler.class.getName());
        setVeilederIdent(getProperty("veileder.username"));
        setOidcCredential(new OidcCredential(getIDToken()));

        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiasyforest")
                .port(8084)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
