package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad;


// TODO: Endre navn til noe som forteller bedre hva det er, f.eks. Sykmeldingperiode
public class Aktivitet {
    public Datospenn periode;
    public int grad;
    // TODO: Endre navn til noe som kommuniserer bedre hva det er et avvik fra, f.eks. AvvikFraSykmeldingperiode
    public Avvik avvik;

    // TODO: Burde sykmeldingperiodene ha en id, slik at vi ikke surrer med de når man har flere?

    public Aktivitet withDatospenn(final Datospenn datospenn) {
        this.periode = datospenn;
        return this;
    }

    public Aktivitet withGrad(final int grad) {
        this.grad = grad;
        return this;
    }

    public Aktivitet withAvvik(final Avvik avvik) {
        this.avvik = avvik;
        return this;
    }
}