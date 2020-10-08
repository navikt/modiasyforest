package no.nav.syfo.consumer

import no.nav.syfo.controller.domain.*
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentSykeforlopperiodeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSSykeforlopperiode
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentSykeforlopperiodeRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class OppfolgingstilfelleConsumer @Inject constructor(
    private val aktorConsumer: AktorConsumer,
    private val sykefravaersoppfoelgingV1: SykefravaersoppfoelgingV1
) {
    fun hentOppfolgingstilfelleperioder(fnr: String, orgnummer: String): List<Oppfolgingstilfelle> {
        val aktorId = aktorConsumer.hentAktoerIdForFnr(fnr)
        val request = WSHentSykeforlopperiodeRequest()
            .withAktoerId(aktorId)
            .withOrgnummer(orgnummer)
        return try {
            val wsHentSykeforlopperiodeResponse = sykefravaersoppfoelgingV1.hentSykeforlopperiode(request)
            tilSykeforlopperiodeListe(wsHentSykeforlopperiodeResponse.sykeforlopperiodeListe, orgnummer)
        } catch (e: HentSykeforlopperiodeSikkerhetsbegrensning) {
            log.warn("Sikkerhetsbegrensning ved henting av oppfølgingstilfelleperioder")
            emptyList()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OppfolgingstilfelleConsumer::class.java)
    }
}

private fun tilSykeforlopperiodeListe(
    wsSykeforlopperiodeListe: List<WSSykeforlopperiode>,
    orgnummer: String
): List<Oppfolgingstilfelle> {
    return wsSykeforlopperiodeListe.map {
        Oppfolgingstilfelle(
            orgnummer = orgnummer,
            fom = it.fom,
            tom = it.tom,
            grad = it.grad,
            aktivitet = it.aktivitet
        )
    }
}
