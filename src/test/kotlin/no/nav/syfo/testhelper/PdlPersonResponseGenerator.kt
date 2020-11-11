package no.nav.syfo.testhelper

import no.nav.syfo.consumer.pdl.*

fun generatePdlPersonNavn(): PdlPersonNavn {
    return PdlPersonNavn(
        fornavn = UserConstants.PERSON_NAME_FIRST,
        mellomnavn = UserConstants.PERSON_NAME_MIDDLE,
        etternavn = UserConstants.PERSON_NAME_LAST
    ).copy()
}

fun generatePdlHentPerson(
    pdlPersonNavn: PdlPersonNavn?
): PdlHentPerson {
    return PdlHentPerson(
        hentPerson = PdlPerson(
            navn = listOf(
                pdlPersonNavn ?: generatePdlPersonNavn()
            )
        )
    ).copy()
}
