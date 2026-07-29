package oppslag

import io.ktor.server.application.Application
import io.ktor.server.engine.ConnectorType
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.runBlocking
import oppslag.fakes.BehandlerFake
import oppslag.fakes.KrrFake
import oppslag.fakes.PdlFake
import oppslag.fakes.SafFake
import oppslag.fakes.texasFake

object Fakes : AutoCloseable {
    private val texas by lazy { embeddedServer(Netty, port = 0, module = Application::texasFake) }

    val krr by lazy { embeddedServer(Netty, port = 0, module = Application::KrrFake) }
    val behandler by lazy { embeddedServer(Netty, port = 0, module = Application::BehandlerFake) }
    val saf by lazy { embeddedServer(Netty, port = 0, module = Application::SafFake) }
    val pdl by lazy { embeddedServer(Netty, port = 0, module = Application::PdlFake) }

    private val started = AtomicBoolean(false)

    fun start() {
        if (!started.compareAndSet(false, true)) {
            return
        }

        krr.start()
        texas.start()
        behandler.start()
        saf.start()
        pdl.start()

        // Texas
        System.setProperty("NAIS_TOKEN_ENDPOINT", "http://localhost:${texas.port()}/token")
        System.setProperty("NAIS_TOKEN_EXCHANGE_ENDPOINT", "http://localhost:${texas.port()}/token/exchange")
        System.setProperty("NAIS_TOKEN_INTROSPECTION_ENDPOINT", "http://localhost:${texas.port()}/introspect")
    }

    override fun close() {
        if (!started.compareAndSet(true, false)) {
            return
        }

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