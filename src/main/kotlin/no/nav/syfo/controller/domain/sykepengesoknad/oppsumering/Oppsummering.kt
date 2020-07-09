package no.nav.syfo.controller.domain.sykepengesoknad.oppsumering

import java.io.Serializable

class Oppsummering : Serializable {
    var soknad: List<Sporsmal>? = null
    var bekreftetKorrektInformasjon: Sporsmal? = null
    var vaerKlarOverAt: Tilleggstekst? = null
    fun withSporsmalsliste(oppsummering: List<Sporsmal>?): Oppsummering {
        soknad = oppsummering
        return this
    }

    fun withBekreftetKorrektInformasjon(bekreftetKorrektInformasjon: Sporsmal?): Oppsummering {
        this.bekreftetKorrektInformasjon = bekreftetKorrektInformasjon
        return this
    }

    fun withAnsvarserklaring(ansvarserklaring: Tilleggstekst?): Oppsummering {
        vaerKlarOverAt = ansvarserklaring
        return this
    }
}
