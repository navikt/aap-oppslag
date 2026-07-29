package oppslag

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.engine.embeddedServer
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.header
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import no.nav.aap.komponenter.server.auth.IdentityProvider
import no.nav.aap.komponenter.server.authentication
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

val LOGGER: Logger = LoggerFactory.getLogger("aap-oppslag")

fun main() {
    Thread.currentThread().setUncaughtExceptionHandler { _, e -> LOGGER.error("Uhåndtert feil", e) }
    embeddedServer(Netty, port = 8080, module = Application::api).start(wait = true)
}

fun Application.api(
    config: Config = Config(),
) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val pdl = PdlGraphQLClient(config.pdlConfig)
    val krr = KrrClient(config.krrConfig)
    val behandler = BehandlerClient(config.behandlerConfig)
    val saf = SafClient(config.safConfig)

    install(MicrometerMetrics) { registry = prometheus }

    authentication(listOf(IdentityProvider.TOKENX, IdentityProvider.ENTRA_ID))

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
        authenticate(IdentityProvider.TOKENX.value) {
            behandlerRoute(behandler)
            krrRoute(krr)
            safRoute(saf)
        }

        pdlRoute(pdl)

        actuator(prometheus)
    }
}
