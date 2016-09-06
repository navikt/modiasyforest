package no.nav.sbl.dialogarena.modiasyforest.config;

import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {

    public RestConfig() {
        super(TestRessurs.class);
    }
}