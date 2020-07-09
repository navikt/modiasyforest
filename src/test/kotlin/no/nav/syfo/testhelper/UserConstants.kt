package no.nav.syfo.testhelper

import no.nav.syfo.mock.AktorMock.Companion.mockAktorId
import no.nav.syfo.mocks.BrukerprofilMock

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
    const val PERSON_NAVN = BrukerprofilMock.PERSON_FORNAVN + " " + BrukerprofilMock.PERSON_ETTERNAVN
}
