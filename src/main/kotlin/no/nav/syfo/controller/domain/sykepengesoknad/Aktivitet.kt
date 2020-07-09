package no.nav.syfo.controller.domain.sykepengesoknad

import java.io.Serializable

// TODO: Endre navn til noe som forteller bedre hva det er, f.eks. Sykmeldingperiode
class Aktivitet : Serializable {
    var periode: Datospenn? = null
    var grad = 0

    // TODO: Endre navn til noe som kommuniserer bedre hva det er et avvik fra, f.eks. AvvikFraSykmeldingperiode
    var avvik: Avvik? = null

    // TODO: Burde sykmeldingperiodene ha en id, slik at vi ikke surrer med de når man har flere?
    fun withDatospenn(datospenn: Datospenn?): Aktivitet {
        periode = datospenn
        return this
    }

    fun withGrad(grad: Int): Aktivitet {
        this.grad = grad
        return this
    }

    fun withAvvik(avvik: Avvik?): Aktivitet {
        this.avvik = avvik
        return this
    }
}
