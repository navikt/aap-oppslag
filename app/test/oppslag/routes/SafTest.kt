package oppslag.routes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.io.readByteArray
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.WithFakes
import oppslag.api
import oppslag.integrasjoner.saf.Dokument
import oppslag.port
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@WithFakes
class SafTest {

    @Test
    fun `Strømmer PDF før SAF har sendt hele dokumentet`() = runBlocking {
        val fortsettSending = CompletableDeferred<Unit>()
        val førsteDel = ByteArray(64 * 1024) { 1 }
        val sisteDel = ByteArray(64 * 1024) { 2 }
        val saf = embeddedServer(Netty, port = 0) {
            routing {
                get("/rest/hentdokument/400000000/23423535/ARKIV") {
                    call.respondBytesWriter(ContentType.Application.Pdf) {
                        writeFully(førsteDel)
                        flush()
                        fortsettSending.await()
                        writeFully(sisteDel)
                    }
                }
            }
        }.start()
        val config = TestConfig.default()
        val oppslag = embeddedServer(Netty, port = 0) {
            api(config.copy(safConfig = config.safConfig.copy(baseUrl = "http://localhost:${saf.port()}")))
        }

        try {
            oppslag.start()
            HttpClient(CIO).use { client ->
                withTimeout(10_000) {
                    client.prepareGet("http://localhost:${oppslag.port()}/dokumenter/400000000/23423535") {
                        bearerAuth(TokenXGen.generate("12345678910"))
                    }.execute { response ->
                        assertEquals(HttpStatusCode.OK, response.status)
                        assertEquals("attachment; filename=23423535.pdf", response.headers[HttpHeaders.ContentDisposition])
                        val kanal = response.bodyAsChannel()
                        val mottatt = ByteArray(førsteDel.size)
                        kanal.readFully(mottatt)
                        assertArrayEquals(førsteDel, mottatt)
                        fortsettSending.complete(Unit)
                        assertArrayEquals(sisteDel, kanal.readRemaining().readByteArray())
                    }
                }
            }
        } finally {
            fortsettSending.complete(Unit)
            oppslag.stop(0, 0)
            saf.stop(0, 0)
        }
    }

    @Test
    fun `Returnerer 404 når PDF ikke finnes i SAF`() {
        testApplication {
            application { api(TestConfig.default()) }

            val response = client.get("/dokumenter/400000000/ukjent") {
                bearerAuth(TokenXGen.generate("12345678910"))
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Henter en journalpost`() {
            testApplication {
                val config = TestConfig.default()
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation) {
                        jackson {
                            registerModule(JavaTimeModule())
                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        }
                    }
                }

                val res = client.get("/dokumenter/1234567") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }.body<List<Dokument>>()

                assertEquals(1, res.size)
            }
    }

    @Test
    fun `Henter ut JSON`() {
            testApplication {
                val config = TestConfig.default()
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation) {
                        jackson {
                            registerModule(JavaTimeModule())
                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        }
                    }
                }

                val res = client.get("/dokumenter/400000000/json") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }.body<ByteArray>()

                assertEquals("{}", String(res))
            }
    }
}