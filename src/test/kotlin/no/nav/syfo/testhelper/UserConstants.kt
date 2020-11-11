package no.nav.syfo.testhelper

object UserConstants {
    const val ARBEIDSTAKER_FNR = "12345678912"
    val ARBEIDSTAKER_AKTORID = mockAktorId(ARBEIDSTAKER_FNR)
    const val LEDER_FNR = "12987654321"
    val LEDER_AKTORID = mockAktorId(LEDER_FNR)
    const val VIRKSOMHETSNUMMER = "123456789"
    const val NAV_ENHET = "0330"
    const val VEILEDER_ID = "Z999999"
    const val PERSON_TLF = "12345678"
    const val PERSON_EMAIL = "test@nav.no"

    const val PERSON_NAME_FIRST = "First"
    const val PERSON_NAME_MIDDLE = "Middle"
    const val PERSON_NAME_LAST = "Last"
    const val PERSON_NAVN_FULL = PERSON_NAME_FIRST + PERSON_NAME_MIDDLE + PERSON_NAME_LAST

    const val STS_TOKEN = "123456789"
}

private const val MOCK_AKTORID_PREFIX = "10"

fun mockAktorId(fnr: String): String {
    return "$MOCK_AKTORID_PREFIX$fnr"
}
