package no.nav.syfo.controller.domain.sykepengesoknad;

import java.io.Serializable;

public class AnnenInntektskilde implements Serializable {
    public boolean sykmeldt;
    public AnnenInntektskildeType annenInntektskildeType;

    public AnnenInntektskilde withSykmeldt(final boolean sykmeldt) {
        this.sykmeldt = sykmeldt;
        return this;
    }

    public AnnenInntektskilde withAnnenInntektskildeType(final AnnenInntektskildeType annenInntektskildeType) {
        this.annenInntektskildeType = annenInntektskildeType;
        return this;
    }
}
