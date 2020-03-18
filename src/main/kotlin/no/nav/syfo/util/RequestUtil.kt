package no.nav.syfo.util

import java.util.*

const val NAV_CONSUMER_ID_HEADER = "Nav-Consumer-Id"
const val APP_CONSUMER_ID = "syfomoteadmin"
const val NAV_CALL_ID_HEADER = "Nav-Call-Id"

const val NAV_PERSONIDENTER_HEADER = "Nav-Personidenter"

fun createCallId(): String = UUID.randomUUID().toString()
