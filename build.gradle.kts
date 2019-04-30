import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val cxfVersion = "3.3.3"
val oidcSpringSupportVersion = "0.2.4"
val springBootVersion = "2.1.8.RELEASE"

val jaxWsApiVersion = "2.3.1"
val javaxAnnotationApiVersion = "1.3.2"
val jaxbApiVersion = "2.4.0-b180830.0359"
val jaxbRuntimeVersion = "2.4.0-b180830.0438"
val javaxActivationVersion = "1.1.1"
val jaxwsToolsVersion = "2.3.1"

plugins {
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.50"
    id("java")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.0")
        classpath("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
        classpath("org.glassfish.jaxb:jaxb-runtime:2.4.0-b180830.0438")
        classpath("com.sun.activation:javax.activation:1.2.0")
        classpath("com.sun.xml.ws:jaxws-tools:2.3.1") {
            exclude(group = "com.sun.xml.ws", module = "policy")
        }
    }
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://repo.adeo.no/repository/maven-releases/")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
    maven(url = "http://packages.confluent.io/maven/")
}

dependencies {
    implementation("javax.xml.ws:jaxws-api:$jaxWsApiVersion")
    implementation("javax.annotation:javax.annotation-api:$javaxAnnotationApiVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation("javax.activation:activation:$javaxActivationVersion")
    implementation("com.sun.xml.ws:jaxws-tools:$jaxwsToolsVersion") {
        exclude(group = "com.sun.xml.ws", module = "policy")
    }

    implementation("no.nav.syfo.tjenester:dkif-tjenestespesifikasjon:1.2")
    implementation("no.nav.sbl.dialogarena:diskresjonskodev1-tjenestespesifikasjon:1.0.0")
    implementation("no.nav.syfo.tjenester:egenAnsatt-v1-tjenestespesifikasjon:1.0.1")
    implementation("no.nav.sbl.dialogarena:organisasjonv4-tjenestespesifikasjon:1.0.1")
    implementation("no.nav.syfo.tjenester:sykmeldingv1-tjenestespesifikasjon:1.1.18")
    implementation("no.nav.tjenestespesifikasjoner:nav-arbeidsforhold-v3-tjenestespesifikasjon:1.2019.03.05-14.13-d95264192bc7")
    implementation("no.nav.syfo.tjenester:aktoer-v2:1.0")
    implementation("no.nav.syfo.tjenester:brukerprofil-v3-tjenestespesifikasjon:3.0.1")
    implementation("no.nav.syfo.tjenester:sykefravaersoppfoelgingv1-tjenestespesifikasjon:1.0.20")
    implementation("no.nav.syfo.tjenester:sykepengesoeknadv1-tjenestespesifikasjon:1.0.17")
    implementation("no.nav.syfo.tjenester:digisyfo-sykepengesoeknadoppsummering:1.0.2")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-jersey:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-logging:$springBootVersion")
    implementation("no.nav.security:oidc-support:$oidcSpringSupportVersion")
    implementation("no.nav.security:oidc-spring-support:$oidcSpringSupportVersion")

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")

    implementation("io.micrometer:micrometer-registry-prometheus:1.0.6")
    implementation("org.springframework.boot:spring-boot-starter-cache:$springBootVersion")
    implementation("javax.ws.rs:javax.ws.rs-api:2.0.1")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("javax.inject:javax.inject:1")
    implementation("net.logstash.logback:logstash-logback-encoder:4.10")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.71")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.2.71")
    implementation("org.projectlombok:lombok:1.16.22")
    annotationProcessor("org.projectlombok:lombok:1.18.6")
    testCompile("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testCompile("no.nav.security:oidc-spring-test:$oidcSpringSupportVersion")
}

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.Application"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        transform(PropertiesFileTransformer::class.java) {
            paths = listOf("META-INF/spring.factories")
            mergeStrategy = "append"
        }
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }
}
