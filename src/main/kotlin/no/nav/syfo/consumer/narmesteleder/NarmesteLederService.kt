package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.consumer.BrukerprofilConsumer
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.consumer.ereg.EregConsumer
import no.nav.syfo.consumer.ereg.Virksomhetsnummer
import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Fodselsnummer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NarmesteLederService @Autowired constructor(
    private val aktorregisterConsumer: AktorregisterConsumer,
    private val brukerprofilConsumer: BrukerprofilConsumer,
    private val eregConsumer: EregConsumer,
    private val narmesteLederConsumer: NarmesteLederConsumer
) {
    fun narmesteLedere(fnr: Fodselsnummer): List<NaermesteLeder> {
        val aktorId = aktorregisterConsumer.aktorId(fnr)
        val narmesteLederRelasjonerLedere = narmesteLederConsumer.narmesteLederRelasjonerLedere(aktorId)

        return narmesteLederRelasjonerLedere.map {
            val lederFnr = aktorregisterConsumer.fodselsnummer(AktorId(it.narmesteLederAktorId))
            val lederNavn = brukerprofilConsumer.hentBruker(lederFnr.value).navn
            val virksomhetsnavn = eregConsumer.virksomhetsnavn(Virksomhetsnummer(it.orgnummer))
            it.toNaermesteLeder(
                navn = lederNavn ?: "",
                virksomhetsnavn = virksomhetsnavn
            )
        }
    }
}
