package no.nav.sbl.dialogarena.modiasyforest.mock;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Kontaktinfo;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.adresse.*;

import java.math.BigInteger;

public class brukerMock {

    public static Bruker brukerMedAdresser() {
        return new Bruker()
                .navn("Sygve sykmeldt")
                .bostedsadresse(new Bostedsadresse()
                        .strukturertAdresse(new StrukturertAdresse()
                                .tilleggsadresse("C/O NAV OSLO")
                                .landkode("NOR")
                                .gateadresse(new Gateadresse()
                                        .postnummer("7000")
                                        .poststed("TRONDHEIM")
                                        .gatenavn("LANGTGATENAVNVEIEN 703B")
                                        .bolignummer(null)
                                        .gatenummer(null)
                                        .husnummer(BigInteger.valueOf(4))
                                        .husbokstav("B")
                                )
                        )
                )
                .midlertidigAdresseNorge(new MidlertidigAdresseNorge()
                        .strukturertAdresse(new StrukturertAdresse()
                                .tilleggsadresse("C/O NAV OSLO")
                                .landkode("NOR")
                                .gateadresse(new Gateadresse()
                                    .gatenavn("LANGTGATENAVNVEIEN 703B")
                                    .bolignummer(null)
                                    .gatenummer(null)
                                    .husnummer(BigInteger.valueOf(4))
                                    .husbokstav("B")
                                    .postnummer("1798")
                                    .poststed("AREMARK")
                                )
                        ))
                .midlertidigAdresseUtland(new MidlertidigAdresseUtland()
                        .ustrukturertAdresse(new UstrukturertAdresse()
                                .landkode("NOR")
                                .adresselinje1("LANGTGATENAVNVEIEN 703B")
                                .adresselinje2(null)
                                .adresselinje3(null)
                                .adresselinje4("2320 FURNES")
                        ))
                .postAdresse(new PostAdresse()
                        .ustrukturertAdresse(new UstrukturertAdresse()
                                .landkode("NOR")
                                .adresselinje1("LANGTGATENAVNVEIEN 703B")
                                .adresselinje2(null)
                                .adresselinje3(null)
                                .adresselinje4("2320 FURNES")
                        ))
                .kontaktinfo(new Kontaktinfo()
                        .fnr("99008811772")
                        .epost("test@nav.no")
                        .tlf("12345678")
                        .skalHaVarsel(true))
                .arbeidssituasjon("ARBEIDSTAKER");
    }
}
