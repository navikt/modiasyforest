package no.nav.syfo.consumer.narmesteleder

import no.nav.syfo.consumer.AktorConsumer
import no.nav.syfo.consumer.BrukerprofilConsumer
import no.nav.syfo.consumer.ereg.EregConsumer
import no.nav.syfo.consumer.ereg.Virksomhetsnummer
import no.nav.syfo.domain.AktorId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NarmesteLederService @Autowired constructor(
    private val aktorConsumer: AktorConsumer,
    private val brukerprofilConsumer: BrukerprofilConsumer,
    private val eregConsumer: EregConsumer,
    private val narmesteLederConsumer: NarmesteLederConsumer
) {
    fun narmesteLedere(fnr: String): List<NaermesteLeder> {
        val aktorId = aktorConsumer.hentAktoerIdForFnr(fnr)
        val narmesteLederRelasjonerLedere = narmesteLederConsumer.narmesteLederRelasjonerLedere(AktorId(aktorId))

        return narmesteLederRelasjonerLedere.map {
            val lederFnr = aktorConsumer.hentFnrForAktoer(AktorId(it.narmesteLederAktorId))
            val lederNavn = brukerprofilConsumer.hentBruker(lederFnr).navn
            val virksomhetsnavn = eregConsumer.virksomhetsnavn(Virksomhetsnummer(it.orgnummer))
            it.toNaermesteLeder(
                navn = lederNavn ?: "",
                virksomhetsnavn = virksomhetsnavn
            )
        }
    }
}
