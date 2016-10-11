package no.nav.sbl.dialogarena.modiasyforest.rest.domain;

import no.nav.tjeneste.virksomhet.behandle.sykmelding.v1.WSFeilaktigOpplysning;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class FeilaktigeOpplysninger {
    public boolean periode;
    public boolean sykmeldingsgrad;
    public boolean arbeidsgiver;
    public boolean diagnose;
    public boolean andre;

    public List<WSFeilaktigOpplysning> tilWSFeilaktigeOpplysningerListe() {
        Predicate<Field> erSattTrue = field -> {
            try {
                return field.getBoolean(this);
            } catch (IllegalAccessException ignore) {
                //Ignorerer exception
            }
            return false;
        };

        return Arrays.stream(this.getClass().getFields())
                .filter(erSattTrue)
                .map(field -> WSFeilaktigOpplysning.fromValue(field.getName().toUpperCase()))
                .collect(toList());
    }

    public FeilaktigeOpplysninger withPeriode(final boolean periode) {
        this.periode = periode;
        return this;
    }

    public FeilaktigeOpplysninger withSykmeldingsgrad(final boolean sykmeldingsgrad) {
        this.sykmeldingsgrad = sykmeldingsgrad;
        return this;
    }

    public FeilaktigeOpplysninger withArbeidsgiver(final boolean arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
        return this;
    }

    public FeilaktigeOpplysninger withDiagnose(final boolean diagnose) {
        this.diagnose = diagnose;
        return this;
    }

    public FeilaktigeOpplysninger withAndre(final boolean andre) {
        this.andre = andre;
        return this;
    }
}

