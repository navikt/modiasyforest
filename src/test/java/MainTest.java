import no.nav.testconfig.ApiAppTest;

import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static void main(String[] args) throws Exception {
        setFrom("jetty-environment.properties");
        setupTestContext(ApiAppTest.Config.builder().applicationName("modiasyforest").build());
        Main.main("8084");
    }

}
