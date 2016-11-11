package no.nav.sbl.dialogarena.modiasyforest.selftest;


import no.nav.sbl.tekster.TeksterAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TeksterInvalideringServlet implements HttpRequestHandler {
    private final Logger logger = LoggerFactory.getLogger(TeksterInvalideringServlet.class);

    @Inject
    @Named("commonTekster")
    TeksterAPI commonTekster;

    @Inject
    @Named("moteTekster")
    TeksterAPI moteTekster;

    @Inject
    @Named("syfoTekster")
    TeksterAPI syfoTekster;

    @Override
    public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        logger.info("Invaliderer tekst-cache");
        commonTekster.invaliderCache();
        moteTekster.invaliderCache();
        syfoTekster.invaliderCache();
    }
}
