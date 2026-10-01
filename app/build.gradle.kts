plugins {
    id("aap.conventions")
    alias(kelvinLibs.plugins.ktor)
    application
}

application {
    mainClass.set("oppslag.AppKt")
}

dependencies {
    implementation(libs.kelvin.server)

    implementation(kelvinLibs.ktor.server.auth)
    implementation(kelvinLibs.ktor.server.auth.jwt)
    implementation(kelvinLibs.ktor.server.call.logging)
    implementation(kelvinLibs.ktor.server.content.negotiation)
    implementation(kelvinLibs.ktor.server.core)
    implementation(kelvinLibs.ktor.server.metrics.micrometer)
    implementation(kelvinLibs.ktor.server.netty)
    constraints {
        implementation(libs.netty.common)
        // CVE-2026-54512
        implementation(libs.jackson.core3)
        implementation(libs.jackson.databind3)
    }
    implementation(kelvinLibs.ktor.server.status.pages)

    implementation(kelvinLibs.ktor.client.auth)
    implementation(kelvinLibs.ktor.client.cio)
    implementation(kelvinLibs.ktor.client.content.negotiation)
    implementation(kelvinLibs.ktor.client.jackson)
    implementation(kelvinLibs.ktor.client.core)
    implementation(kelvinLibs.ktor.client.logging)
    implementation("io.ktor:ktor-server-routing-openapi:${kelvinLibs.versions.ktor.get()}")

    implementation(kelvinLibs.micrometer.prometheus)
    implementation(libs.prometheus.metrics.core)
    implementation(kelvinLibs.ktor.serialization.jackson)
    implementation(kelvinLibs.jackson.datatype.jsr310)
    implementation(kelvinLibs.logback.classic)
    implementation(kelvinLibs.logstash.logback.encoder)
    implementation(kelvinLibs.nimbus.jose.jwt)

    testImplementation(kotlin("test"))
    testImplementation(kelvinLibs.ktor.server.test.host)
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}
