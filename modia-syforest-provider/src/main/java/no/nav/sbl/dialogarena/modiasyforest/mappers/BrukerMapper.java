package no.nav.sbl.dialogarena.modiasyforest.mappers;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Bruker;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.adresse.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*;

import java.util.function.Function;

import static no.nav.sbl.java8utils.MapUtil.mapNullable;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class BrukerMapper {

    public static Function<WSPerson, Bruker> ws2bruker = wsPerson ->
            new Bruker()
                    .navn(hentNavnTilPerson(wsPerson))
                    .bostedsadresse(kanskjeBostedsadresse(wsPerson))
                    .midlertidigAdresseNorge(kanskjeMidlertidigAdresseNorge(wsPerson))
                    .midlertidigAdresseUtland(kanskjeMidlertidigAdresseUtland(wsPerson))
                    .postAdresse(kanskjePostAdresse(wsPerson));

    private static String hentNavnTilPerson(WSPerson wsPerson) {
        String mellomnavn = wsPerson.getPersonnavn().getMellomnavn() == null ? "" : wsPerson.getPersonnavn().getMellomnavn();
        if (!"".equals(mellomnavn)) {
            mellomnavn = mellomnavn + " ";
        }
        final String navnFraTps = wsPerson.getPersonnavn().getFornavn() + " " + mellomnavn + wsPerson.getPersonnavn().getEtternavn();
        return capitalize(navnFraTps.toLowerCase(), '-', ' ');
    }

    private static Bostedsadresse kanskjeBostedsadresse(WSPerson person) {
        WSBostedsadresse wsBostedsadresse = person.getBostedsadresse();
        return wsBostedsadresse != null ? new Bostedsadresse()
                .strukturertAdresse(mapStrukturertAdresse(wsBostedsadresse.getStrukturertAdresse())) : null;
    }

    private static MidlertidigAdresseNorge kanskjeMidlertidigAdresseNorge(WSPerson person) {
        if (person instanceof WSBruker) {
            WSMidlertidigPostadresse wsMidlertidigPostadresse = ((WSBruker) person).getMidlertidigPostadresse();
            if (wsMidlertidigPostadresse != null && wsMidlertidigPostadresse instanceof WSMidlertidigPostadresseNorge) {
                new MidlertidigAdresseNorge()
                        .strukturertAdresse(mapStrukturertAdresse(((WSMidlertidigPostadresseNorge) wsMidlertidigPostadresse).getStrukturertAdresse()));
            }
        }
        return null;
    }

    private static MidlertidigAdresseUtland kanskjeMidlertidigAdresseUtland(WSPerson person) {
        if (person instanceof WSBruker) {
            WSMidlertidigPostadresse wsMidlertidigPostadresse = ((WSBruker) person).getMidlertidigPostadresse();
            if (wsMidlertidigPostadresse != null && wsMidlertidigPostadresse instanceof WSMidlertidigPostadresseUtland) {
                return new MidlertidigAdresseUtland()
                        .ustrukturertAdresse(mapNullable(((WSMidlertidigPostadresseUtland) wsMidlertidigPostadresse).getUstrukturertAdresse(), tilUstrukturertAdresse));
            }
        }
        return null;
    }

    private static PostAdresse kanskjePostAdresse(WSPerson person) {
        WSPostadresse wsPostadresse = person.getPostadresse();
        return wsPostadresse != null ? new PostAdresse().ustrukturertAdresse(mapNullable(wsPostadresse.getUstrukturertAdresse(), tilUstrukturertAdresse)) : null;
    }

    private static StrukturertAdresse mapStrukturertAdresse(WSStrukturertAdresse wsStrukturertadresse) {
        StrukturertAdresse strukturertAdresse = mapNullable(wsStrukturertadresse, tilStrukturertAdresse);
        if (wsStrukturertadresse instanceof WSGateadresse) {
            strukturertAdresse.gateadresse(mapNullable((WSGateadresse) wsStrukturertadresse, tilGateAdresse));
        } else if (wsStrukturertadresse instanceof WSPostboksadresseNorsk) {
            strukturertAdresse.postboksadresseNorsk(mapNullable((WSPostboksadresseNorsk) wsStrukturertadresse, tilPostboksadresseNorsk));
        } else if (wsStrukturertadresse instanceof WSMatrikkeladresse) {
            strukturertAdresse.matrikkeladresse(mapNullable((WSMatrikkeladresse) wsStrukturertadresse, tilMatrikkeladresse));
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