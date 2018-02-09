package no.nav.sbl.dialogarena.modiasyforest.utils;


import no.nav.melding.virksomhet.sykepengesoeknadoppsummering.v1.sykepengesoeknadoppsummering.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.JAXBContext.newInstance;
import static javax.xml.bind.Marshaller.*;

public class JAXB {

    private static final JAXBContext SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT;

    static {
        try {
            SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT = newInstance(
                    ObjectFactory.class
            );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshalSykepengesoknadOppsummering(String melding) {
        try {
            return (T) SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT.createUnmarshaller().unmarshal(new StringReader(melding));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
