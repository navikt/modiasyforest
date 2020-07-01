# Modiasyforest - Modia sykefraværsoppfølgingen

Modiasyforest er et REST-API for hvor veileder kan gjøre oppslag for informasjon om en brukere og brukeres sykefravær.

Applikasjonen kommuniserer med syfoservice og andre tjenester i FSS via WS.

#Lokal utvikling
Start opp via `LocalApplication.main`. Kjører på port 8084.

# Pipeline

Pipeline er på Github Action.
Commits til Master-branch deployes automatisk til dev-fss og prod-fss.
Commits til ikke-master-branch bygges uten automatisk deploy.

#Redis Cache
modiasyforest bruker redis for cache.
Redis pod må startes manuelt ved å kjøre følgdende kommando: `kubectl apply -f redis-config.yaml`.

## Hente pakker fra Github Package Registry
Noen pakker hentes fra Github Package Registry som krever autentisering.
Pakkene kan lastes ned via build.gradle slik:
```
val githubUser: String by project
val githubPassword: String by project
repositories {
     maven {
        url = uri("https://maven.pkg.github.com/navikt/syfotjenester")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/tjenestespesifikasjoner")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}
```

`githubUser` og `githubPassword` settes i `~/.gradle/gradle.properties`:

```
githubUser=x-access-token
githubPassword=<token>
```

Hvor `<token>` er et personal access token med scope `read:packages`(og SSO enabled).

Evt. kan variablene kan også konfigureres som miljøvariabler eller brukes i kommandolinjen:

* `ORG_GRADLE_PROJECT_githubUser`
* `ORG_GRADLE_PROJECT_githubPassword`

```
./gradlew -PgithubUser=x-access-token -PgithubPassword=[token]
```
