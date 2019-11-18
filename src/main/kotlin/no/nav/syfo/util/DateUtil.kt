package no.nav.syfo.util

import java.time.LocalDateTime
import java.time.Month

fun parseLocalDateTime(date: String): LocalDateTime {
    val year = Integer.parseInt(date.substring(0, 4))
    val month = Month.of(Integer.parseInt(date.substring(5, 7)))
    val day = Integer.parseInt(date.substring(8, 10))
    return LocalDateTime.of(year, month, day, 0, 0)
}
