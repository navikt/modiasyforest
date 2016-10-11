package no.nav.sbl.dialogarena.modiasyforest.rest.domain;

public class Innsendingsparametre {
    public FeilaktigeOpplysninger feilaktigeOpplysninger;
    public String orgnummer;
    public String arbeidssituasjon;

    public Innsendingsparametre withOrgnummer(final String orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public Innsendingsparametre withFeilaktigeOpplysninger(final FeilaktigeOpplysninger feilaktigeOpplysninger) {
        this.feilaktigeOpplysninger = feilaktigeOpplysninger;
        return this;
    }

    public Innsendingsparametre withArbeidssituasjon(final String arbeidssituasjon) {
        this.arbeidssituasjon = arbeidssituasjon;
        return this;
    }
}



