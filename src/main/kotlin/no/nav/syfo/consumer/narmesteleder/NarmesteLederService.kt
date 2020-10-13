package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.consumer.ereg.EregConsumer
import no.nav.syfo.consumer.ereg.Virksomhetsnummer
import no.nav.syfo.consumer.pdl.*
import no.nav.syfo.controller.narmesteleder.NaermesteLeder
import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Fodselsnummer
import no.nav.syfo.util.createCallId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NarmesteLederService @Autowired constructor(
    private val eregConsumer: EregConsumer,
    private val narmesteLederConsumer: NarmesteLederConsumer,
    private val pdlConsumer: PdlConsumer
) {
    fun narmesteLedere(fnr: Fodselsnummer): List<NaermesteLeder> {
        val aktorId = pdlConsumer.aktorId(fnr, createCallId())
        val narmesteLederRelasjonerLedere = narmesteLederConsumer.narmesteLederRelasjonerLedere(aktorId)

        return narmesteLederRelasjonerLedere.map {
            val narmesteLederAktorId = AktorId(it.narmesteLederAktorId)
            val lederFnr = pdlConsumer.fodselsnummer(narmesteLederAktorId, createCallId())
            val lederNavn = pdlConsumer.person(lederFnr)?.fullName() ?: ""
            val virksomhetsnavn = eregConsumer.virksomhetsnavn(Virksomhetsnummer(it.orgnummer))
            it.toNaermesteLeder(
                navn = lederNavn,
                virksomhetsnavn = virksomhetsnavn
            )
        }
    }
}
