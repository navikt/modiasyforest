import no.nav.testconfig.ApiAppTest;

import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static void main(String[] args) throws Exception {
        setupTestContext(ApiAppTest.Config.builder().applicationName("modiasyforest").build());
        setFrom("jetty-environment.properties");
        Main.main("8084");
    }

}
