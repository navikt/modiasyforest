# Modiasyforest - Modia sykefraværsoppfølgingen

Modiasyforest er et REST-API for "Sykefraværsoppfølging" som skal tilgjengliggjøres for NAV-veiledere. APIet
tilgjengeliggjør endepunkter for å hente informasjon om en brukers sykefravær og gjør det mulig for veileder å sende
møteforespørsler. Det er også her ledetekster til bruk i frontend-applikasjonen ligger.

Applikasjonen kommuniserer med syfoservice via WS og moteadmin via REST.

#Lokal utvikling
Start opp via `MainTest.main`. Kjører på port 8084.

#Veien til prod
Bygg og Pipeline jobber ligger i jenkins: http://bekkci.devillo.no/view/alle-jobber/job/digisyfo/job/modiasyforest/

Seeds ligger på github: https://github.com/navikt/jenkins-dsl-scripts