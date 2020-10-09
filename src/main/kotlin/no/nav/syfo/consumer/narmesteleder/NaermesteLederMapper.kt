package no.nav.syfo.consumer.narmesteleder

import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder
import org.apache.commons.lang3.text.WordUtils

fun tilNaermesteLeder(response: WSNaermesteLeder, organisasjon: String): NaermesteLeder {
    return NaermesteLeder()
        .withNavn(WordUtils.capitalize(response.navn))
        .withEpost(response.epost)
        .withTlf(response.mobil)
        .withFomDato(response.naermesteLederStatus.aktivFom)
        .withOrgnummer(response.orgnummer)
        .withOrganisasjonsnavn(organisasjon)
        .withAktivTom(response.naermesteLederStatus.aktivTom)
        .withArbeidsgiverForskuttererLoenn(response.isArbeidsgiverForskuttererLoenn)
        .withErOppgitt(true)
}
