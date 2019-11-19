package no.nav.syfo.consumer.veilederoppgaver

data class UpdateVeilederoppgave(
        var uuid: String?,
        var id: Long?,
        var tildeltIdent: String?,
        var sistEndretAv: String,
        var status: String
)
