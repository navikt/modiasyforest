package no.nav.syfo.consumer.syketilfelle.domain

import no.nav.syfo.controller.oppfolgingstilfelle.domain.Oppfolgingstilfelle
import java.time.LocalDateTime

data class KOppfolgingstilfelle(
    val aktorId: String,
    val orgnummer: String,
    val tidslinje: List<KSyketilfelledag>,
    val sisteDagIArbeidsgiverperiode: KSyketilfelledag,
    val antallBrukteDager: Int,
    val oppbruktArbeidsgvierperiode: Boolean,
    val utsendelsestidspunkt: LocalDateTime
)

fun KOppfolgingstilfelle.toOppfolgingstilfelle(): Oppfolgingstilfelle {
    return Oppfolgingstilfelle(
        orgnummer = this.orgnummer,
        fom = this.tidslinje.first().dag,
        tom = this.tidslinje.last().dag
    )
}
