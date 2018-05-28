package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;

public class MottakendeArbeidsgiver {
    public String navn;
    public String virksomhetsnummer;
    public String juridiskOrgnummer;

    public MottakendeArbeidsgiver withNavn(final String navn) {
        this.navn = navn;
        return this;
    }

    public MottakendeArbeidsgiver withVirksomhetsnummer(final String virksomhetsnummer) {
        this.virksomhetsnummer = virksomhetsnummer;
        return this;
    }

    public MottakendeArbeidsgiver withJuridiskOrgnummer(final String juridiskOrgnummer) {
        this.juridiskOrgnummer = juridiskOrgnummer;
        return this;
    }
}
