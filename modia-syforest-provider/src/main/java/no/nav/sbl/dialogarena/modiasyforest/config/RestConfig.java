package no.nav.sbl.dialogarena.modiasyforest.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {

    public RestConfig() {
        packages("no.nav.sbl.dialogarena.modiasyforest.rest");
        register(JacksonJaxbJsonProvider.class);
    }
}