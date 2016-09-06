import no.nav.sbl.dialogarena.common.jetty.Jetty;

import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.common.jetty.JettyStarterUtils.*;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 * <p/>
 * NB!
 * Sett start.properties for å styre integrasjon.
 */
public class StartJetty {

    public static void main(String[] args) {
        runJetty();
    }

    private static void runJetty() {
        Jetty jetty = usingWar()
                .at("modiasyforest")
                .port(8084)
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }


}
