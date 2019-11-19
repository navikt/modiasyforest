package no.nav.syfo.consumer.veilederoppgaver

import no.nav.syfo.util.parseLocalDateTime
import java.time.LocalDateTime

data class Veilederoppgave(
        var id: Long,
        var type: String,
        var tildeltIdent: String?,
        var tildeltEnhet: String?,
        var lenke: String?,
        var fnr: String,
        var virksomhetsnummer: String?,
        var created: String?,
        var sistEndret: String?,
        var sistEndretAv: String?,
        var status: String?,
        var uuid: String?
)

fun Veilederoppgave.getCreated(): LocalDateTime? {
    return created?.let { parseLocalDateTime(it) }
}

fun Veilederoppgave.getSistEndret(): LocalDateTime? {
    return sistEndret?.let { parseLocalDateTime(it) }
}
