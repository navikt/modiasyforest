package no.nav.syfo.util

import java.util.*

fun bearerHeader(token: String): String {
    return "Bearer $token"
}
