package no.nav.syfo.mappers;

import no.nav.syfo.controller.domain.Bruker;
import no.nav.syfo.controller.domain.adresse.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static no.nav.syfo.utils.MapUtil.map;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class BrukerMapper {

    public static Function<WSBruker, Bruker> ws2bruker = wsPerson ->
            new Bruker()
                    .navn(hentNavnTilPerson(wsPerson))
                    .bostedsadresse(kanskjeBostedsadresse(wsPerson).orElse(null))
                    .midlertidigAdresseNorge(kanskjeMidlertidigAdresseNorge(wsPerson).orElse(null))
                    .midlertidigAdresseUtland(kanskjeMidlertidigAdresseUtland(wsPerson).orElse(null))
                    .postAdresse(kanskjePostAdresse(wsPerson).orElse(null));

    private static String hentNavnTilPerson(WSPerson wsPerson) {
        String mellomnavn = ofNullable(wsPerson.getPersonnavn().getMellomnavn()).map(s -> s + " ").orElse("");
        String navnFraTps = wsPerson.getPersonnavn().getFornavn() + " " + mellomnavn + wsPerson.getPersonnavn().getEtternavn();
        return capitalize(navnFraTps.toLowerCase(), '-', ' ');
    }

    private static Optional<Bostedsadresse> kanskjeBostedsadresse(WSPerson person) {
        return ofNullable(person.getBostedsadresse())
                .map(bostedsadresse -> new Bostedsadresse()
                        .strukturertAdresse(mapStrukturertAdresse(bostedsadresse.getStrukturertAdresse())));
    }

    private static Optional<MidlertidigAdresseNorge> kanskjeMidlertidigAdresseNorge(WSBruker person) {
        return ofNullable(person.getMidlertidigPostadresse())
                .filter(postadresse -> postadresse instanceof WSMidlertidigPostadresseNorge)
                .map(postadresse -> (WSMidlertidigPostadresseNorge) postadresse)
                .map(postadresse -> new MidlertidigAdresseNorge()
                        .strukturertAdresse(mapStrukturertAdresse(postadresse.getStrukturertAdresse())));
    }

    private static Optional<MidlertidigAdresseUtland> kanskjeMidlertidigAdresseUtland(WSBruker person) {
        return ofNullable(person.getMidlertidigPostadresse())
                .filter(postadresse -> postadresse instanceof WSMidlertidigPostadresseUtland)
                .map(postadresse -> (WSMidlertidigPostadresseUtland) postadresse)
                .map(postadresse -> new MidlertidigAdresseUtland()
                        .ustrukturertAdresse(map(postadresse.getUstrukturertAdresse(), tilUstrukturertAdresse)));
    }

    private static Optional<PostAdresse> kanskjePostAdresse(WSBruker person) {
        return ofNullable(person.getPostadresse())
                .map(postadresse -> new PostAdresse().ustrukturertAdresse(map(postadresse.getUstrukturertAdresse(), tilUstrukturertAdresse)));
    }

    private static StrukturertAdresse mapStrukturertAdresse(WSStrukturertAdresse wsStrukturertadresse) {
        StrukturertAdresse strukturertAdresse = map(wsStrukturertadresse, tilStrukturertAdresse);
        if (wsStrukturertadresse instanceof WSGateadresse) {
            strukturertAdresse.gateadresse(map((WSGateadresse) wsStrukturertadresse, tilGateAdresse));
        } else if (wsStrukturertadresse instanceof WSPostboksadresseNorsk) {
            strukturertAdresse.postboksadresseNorsk(map((WSPostboksadresseNorsk) wsStrukturertadresse, tilPostboksadresseNorsk));
        } else if (wsStrukturertadresse instanceof WSMatrikkeladresse) {
            strukturertAdresse.matrikkeladresse(map((WSMatrikkeladresse) wsStrukturertadresse, tilMatrikkeladresse));
        }
        return strukturertAdresse;
    }

    private static Function<WSStrukturertAdresse, StrukturertAdresse> tilStrukturertAdresse = wsStrukturertAdresse ->
            new StrukturertAdresse()
                    .landkode(wsStrukturertAdresse.getLandkode().getValue())
                    .tilleggsadresse(wsStrukturertAdresse.getTilleggsadresse());

    private static Function<WSMatrikkeladresse, Matrikkeladresse> tilMatrikkeladresse = wsMatrikkeladresse ->
            new Matrikkeladresse()
                    .eiendomsnavn(wsMatrikkeladresse.getEiendomsnavn())
                    .gardsnummer(wsMatrikkeladresse.getMatrikkelnummer().getGaardsnummer())
                    .bruksnummer(wsMatrikkeladresse.getMatrikkelnummer().getBruksnummer())
                    .festenummer(wsMatrikkeladresse.getMatrikkelnummer().getFestenummer())
                    .seksjonsnummer(wsMatrikkeladresse.getMatrikkelnummer().getSeksjonsnummer())
                    .undernummer(wsMatrikkeladresse.getMatrikkelnummer().getUndernummer())
                    .postnummer(wsMatrikkeladresse.getPoststed().getValue());

    private static Function<WSPostboksadresseNorsk, PostboksadresseNorsk> tilPostboksadresseNorsk = wsPostboksadresseNorsk ->
            new PostboksadresseNorsk()
                    .postboksnummer(wsPostboksadresseNorsk.getPoststed().getValue())
                    .postboksanlegg(wsPostboksadresseNorsk.getPostboksanlegg())
                    .postboksnummer(wsPostboksadresseNorsk.getPostboksnummer());

    private static Function<WSGateadresse, Gateadresse> tilGateAdresse = wsGateadresse ->
            new Gateadresse()
                    .gatenavn(wsGateadresse.getGatenavn())
                    .husnummer(wsGateadresse.getHusnummer())
                    .husbokstav(wsGateadresse.getHusbokstav())
                    .gatenummer(wsGateadresse.getGatenummer())
                    .kommunenummer(wsGateadresse.getKommunenummer())
                    .postnummer(wsGateadresse.getPoststed().getValue());

    private static Function<WSUstrukturertAdresse, UstrukturertAdresse> tilUstrukturertAdresse = wsUstrukturertAdresse ->
            new UstrukturertAdresse()
                    .adresselinje1(wsUstrukturertAdresse.getAdresselinje1())
                    .adresselinje2(wsUstrukturertAdresse.getAdresselinje2())
                    .adresselinje3(wsUstrukturertAdresse.getAdresselinje3())
                    .adresselinje4(wsUstrukturertAdresse.getAdresselinje4());
}
