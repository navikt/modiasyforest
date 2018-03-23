package no.nav.sbl.dialogarena.modiasyforest.utils;


import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class DistinctFilter<T, U> {
    private Set<U> set = new HashSet<>();

    public Predicate<T> on(Function<T, U> ext) {
        return t -> set.add(ext.apply(t));
    }
}

