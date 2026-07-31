package oppslag.routes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import java.util.UUID
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.WithFakes
import oppslag.api
import oppslag.integrasjoner.saf.Dokument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@WithFakes
class SafTest {

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