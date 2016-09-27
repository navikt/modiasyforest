package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.arbeidsgiver.Naermesteleder;

import java.util.List;

import static java.util.Arrays.asList;

public class NaermesteLederService {

    public List<Naermesteleder> hentNaermesteleder(String fnr) {
        return asList(
            new Naermesteleder().withNavn("John").withEpost("test@nav.no").withTlf("12345678")
        );
    }
}
