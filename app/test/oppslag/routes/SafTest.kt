package oppslag.routes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import oppslag.Fakes
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.api
import oppslag.integrasjoner.saf.Dokument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class SafTest {

    @Test
    fun `Henter en journalpost`() {
        Fakes().use { fakes ->
            testApplication {
                val config = TestConfig.default(fakes)
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation) {
                        jackson {
                            registerModule(JavaTimeModule())
                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        }
                    }
                }

                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/dokumenter/1234567") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }.body<List<Dokument>>()

                assertEquals(1, res.size)
            }
        }
    }
}