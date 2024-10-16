package oppslag

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import oppslag.fakes.*

class Fakes : AutoCloseable {
    val krr = embeddedServer(Netty, port = 0, module = Application::KrrFake).apply { start() }
    val tokenx = embeddedServer(Netty, port = 0, module = Application::TokenXFake).apply { start() }
    val behandler =
        embeddedServer(Netty, port = 0, module = Application::BehandlerFake).apply { start() }
    val saf = embeddedServer(Netty, port = 0, module = Application::SafFake).apply { start() }
    val azure = embeddedServer(Netty, port = 0, module = Application::AzureAdFake).apply { start() }
    val pdl = embeddedServer(Netty, port = 0, module = Application::PdlFake).apply { start() }

    override fun close() {
        krr.stop(0L, 0L)
        tokenx.stop(0L, 0L)
        behandler.stop(0L, 0L)
        saf.stop(0L, 0L)
        azure.stop(0L, 0L)
        pdl.stop(0L, 0L)
    }
}

fun EmbeddedServer<*, *>.port(): Int {
    return runBlocking {
        this@port.engine.resolvedConnectors()
    }.first { it.type == ConnectorType.HTTP }
        .port
}