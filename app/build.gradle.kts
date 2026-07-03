plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("aap.conventions")
    alias(libs.plugins.ktor)
    application
}

application {
    mainClass.set("oppslag.AppKt")
}

dependencies {
    implementation(libs.ktorServerAuth)
    implementation(libs.ktorServerAuthJwt)
    implementation(libs.ktorServerCallLogging)
    implementation(libs.ktorServerCallLoggingJvm)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorServerCore)
    implementation(libs.ktorServerMetricsMicrometer)
    implementation(libs.ktorServerNetty)
    constraints {
        implementation(libs.nettyCommon)
        // CVE-2026-54512
        implementation(libs.jacksonCore3)
        implementation(libs.jacksonDatabind3)
    }
    implementation(libs.ktorServerStatusPages)

    implementation(libs.ktorClientAuth)
    implementation(libs.ktorClientCio)
    implementation(libs.ktorClientContentNegotiation)
    implementation(libs.ktorClientJackson)
    implementation(libs.ktorClientCore)
    implementation(libs.ktorClientLogging)
    implementation(libs.ktorServerRoutingOpenapi)

    implementation(libs.micrometerRegistryPrometheus)
    implementation(libs.prometheusMetricsCore)
    implementation(libs.ktorSerializationJackson)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.logbackClassic)
    implementation(libs.logstashLogbackEncoder)
    implementation(libs.nimbusJoseJwt)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktorServerTestHost)
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}
