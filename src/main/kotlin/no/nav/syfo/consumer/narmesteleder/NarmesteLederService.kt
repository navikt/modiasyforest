package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.consumer.ereg.EregConsumer
import no.nav.syfo.consumer.ereg.Virksomhetsnummer
import no.nav.syfo.controller.narmesteleder.NaermesteLeder
import no.nav.syfo.domain.Fodselsnummer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NarmesteLederService @Autowired constructor(
    private val eregConsumer: EregConsumer,
    private val narmesteLederConsumer: NarmesteLederConsumer
) {
    fun narmesteLedere(fnr: Fodselsnummer): List<NaermesteLeder> {
        val narmesteLederRelasjonerLedere = narmesteLederConsumer.narmesteLederRelasjonerLedere(fnr)

        return narmesteLederRelasjonerLedere.map {
            val virksomhetsnavn = eregConsumer.virksomhetsnavn(Virksomhetsnummer(it.narmesteLederRelasjon.orgnummer))
            it.narmesteLederRelasjon.toNaermesteLeder(
                virksomhetsnavn = virksomhetsnavn
            )
        }
    }
}
