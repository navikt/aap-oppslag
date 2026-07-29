package oppslag

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import oppslag.fakes.*

class Fakes : AutoCloseable {
    val krr = embeddedServer(Netty, port = 0, module = Application::KrrFake).apply { start() }
    val texas = embeddedServer(Netty, port = 0, module = Application::texasFake).apply { start() }
    val behandler =
        embeddedServer(Netty, port = 0, module = Application::BehandlerFake).apply { start() }
    val saf = embeddedServer(Netty, port = 0, module = Application::SafFake).apply { start() }
    val pdl = embeddedServer(Netty, port = 0, module = Application::PdlFake).apply { start() }

    init {
        // Texas
        System.setProperty("NAIS_TOKEN_ENDPOINT", "http://localhost:${texas.port()}/token")
        System.setProperty("NAIS_TOKEN_EXCHANGE_ENDPOINT", "http://localhost:${texas.port()}/token/exchange")
        System.setProperty("NAIS_TOKEN_INTROSPECTION_ENDPOINT", "http://localhost:${texas.port()}/introspect")
    }

    override fun close() {
        krr.stop(0L, 0L)
        texas.stop(0L, 0L)
        behandler.stop(0L, 0L)
        saf.stop(0L, 0L)
        pdl.stop(0L, 0L)
    }
}

fun EmbeddedServer<*, *>.port(): Int {
    return runBlocking {
        this@port.engine.resolvedConnectors()
    }.first { it.type == ConnectorType.HTTP }
        .port
}