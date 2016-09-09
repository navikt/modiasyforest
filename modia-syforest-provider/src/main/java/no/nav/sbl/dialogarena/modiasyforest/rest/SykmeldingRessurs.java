package no.nav.sbl.dialogarena.modiasyforest.rest;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding.Sykmelding;
import no.nav.sbl.dialogarena.modiasyforest.services.SykmeldingService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/sykmeldinger")
@Produces(APPLICATION_JSON)
public class SykmeldingRessurs {


    @Inject
    private SykmeldingService sykmeldingService;

    @GET
    public List<Sykmelding> hentSykmeldinger(@QueryParam("fnr") String fnr) {
        return sykmeldingService.hentSykmeldinger(fnr);
    }
}
