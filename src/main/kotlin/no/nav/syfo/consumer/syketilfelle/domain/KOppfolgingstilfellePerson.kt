package no.nav.syfo.consumer.syketilfelle.domain

import no.nav.syfo.controller.oppfolgingstilfelle.domain.OppfolgingstilfellePerson
import java.time.LocalDateTime

data class KOppfolgingstilfellePerson(
    val aktorId: String,
    val tidslinje: List<KSyketilfelledag>,
    val sisteDagIArbeidsgiverperiode: KSyketilfelledag,
    val antallBrukteDager: Int,
    val oppbruktArbeidsgvierperiode: Boolean,
    val utsendelsestidspunkt: LocalDateTime
)

fun KOppfolgingstilfellePerson.toOppfolgingstilfellePerson(): OppfolgingstilfellePerson {
    return OppfolgingstilfellePerson(
        fom = this.tidslinje.first().dag,
        tom = this.tidslinje.last().dag
    )
}
