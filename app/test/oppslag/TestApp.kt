@file:OptIn(ExperimentalKtorApi::class)

package oppslag

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.ExperimentalKtorApi
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import oppslag.auth.TOKENX
import oppslag.auth.authentication
import oppslag.integrasjoner.behandler.BehandlerClient
import oppslag.integrasjoner.krr.KrrClient
import oppslag.integrasjoner.pdl.PdlException
import oppslag.integrasjoner.pdl.PdlGraphQLClient
import oppslag.integrasjoner.saf.DokumentIkkeFunnet
import oppslag.integrasjoner.saf.SafClient
import oppslag.integrasjoner.saf.SafException
import oppslag.routes.actuator
import oppslag.routes.behandlerRoute
import oppslag.routes.krrRoute
import oppslag.routes.pdlRoute
import oppslag.routes.safRoute
import org.slf4j.event.Level

fun main() {

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> LOGGER.error("Uhåndtert feil", e) }
    embeddedServer(Netty, port = 8082, module = Application::api).start(wait = true)
}

private val json = Json { prettyPrint = true }

fun Application.api(
    config: Config = TestConfig.default(Fakes()),
) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val pdl = PdlGraphQLClient(config.tokenx, config.azureConfig, config.pdlConfig)
    val krr = KrrClient(config.tokenx, config.krrConfig)
    val behandler = BehandlerClient(config.tokenx, config.behandlerConfig)
    val saf = SafClient(config.tokenx, config.safConfig)

    install(MicrometerMetrics) { registry = prometheus }

    authentication(config.tokenx)
    authentication(config.azureConfig)

    install(CallLogging) {
        level = Level.INFO
        logger = LOGGER
        format { call ->
            """
                URL:            ${call.request.local.uri}
                Status:         ${call.response.status()}
                Method:         ${call.request.httpMethod.value}
                User-agent:     ${call.request.headers["User-Agent"]}
                CallId:         ${call.request.header("x-callId") ?: call.request.header("nav-callId")}
            """.trimIndent()
        }
        filter { call -> call.request.path().startsWith("/actuator").not() }
    }

    install(StatusPages) {
        exception<PdlException> { call, cause ->
            LOGGER.error("Uhåndtert feil ved kall til '{}'", call.request.local.uri, cause)
            call.respondText(text = "Feil i PDL: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
        exception<SafException> { call, cause ->
            LOGGER.error("Uhåndtert feil ved kall til '{}'", call.request.local.uri, cause)
            call.respondText(text = "${cause.message}", status = HttpStatusCode.InternalServerError)
        }
        exception<DokumentIkkeFunnet> { call, cause ->
            call.respondText(text = "${cause.message}", status = HttpStatusCode.NotFound)
        }
        exception<Throwable> { call, cause ->
            LOGGER.error("Uhåndtert feil ved kall til '{}', cause: '{}'", call.request.local.uri, cause.message, cause)
            call.respondText(text = "Feil i tjeneste: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        route("/openapi.json") {
            get {
                val allRoutes = call.application.plugin(RoutingRoot.Plugin).getAllRoutes()
                val spec = OpenApiDoc.build {
                    info = OpenApiInfo(title = "aap-oppslag", version = "1.0")
                } + allRoutes
                call.respondText(
                    json.encodeToString(OpenApiDoc.serializer(), spec),
                    ContentType.Application.Json
                )
            }
        }.hide()
        route("/test/local-token") {
            get {
                val token = TokenXGen(config.tokenx).generate("08486725851")
                call.respond(token)
            }
        }.hide()

        authenticate(TOKENX) {
            behandlerRoute(behandler)
            krrRoute(krr)
            safRoute(saf)
        }

        pdlRoute(pdl)

        actuator(prometheus)
    }
}
