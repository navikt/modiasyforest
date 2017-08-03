package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykepengesoknad;

public class AnnenInntektskilde {
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