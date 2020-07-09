package no.nav.syfo.util

import no.nav.melding.virksomhet.sykepengesoeknadoppsummering.v1.sykepengesoeknadoppsummering.ObjectFactory
import java.io.StringReader
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException

object JAXB {
    private var SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT: JAXBContext
    @JvmStatic
    fun <T> unmarshalSykepengesoknadOppsummering(melding: String?): T {
        return try {
            SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT.createUnmarshaller().unmarshal(StringReader(melding)) as T
        } catch (e: JAXBException) {
            throw RuntimeException(e)
        }
    }

    init {
        SYKEPENGESOEKNAD_OPPSUMMERING_CONTEXT = try {
            JAXBContext.newInstance(
                ObjectFactory::class.java
            )
        } catch (e: JAXBException) {
            throw RuntimeException(e)
        }
    }
}
