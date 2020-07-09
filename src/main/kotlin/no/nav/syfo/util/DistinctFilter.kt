package no.nav.syfo.util

import java.util.*
import java.util.function.Function
import java.util.function.Predicate

class DistinctFilter<T, U> {
    private val set: MutableSet<U> = HashSet()
    fun on(ext: Function<T, U>): Predicate<T> {
        return Predicate { t: T -> set.add(ext.apply(t)) }
    }
}
