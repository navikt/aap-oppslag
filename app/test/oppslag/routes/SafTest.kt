package oppslag.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import oppslag.Fakes
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.api
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class SafTest {

    @Test
    fun `Kan hente dokument`() {
        Fakes().use { fakes ->
            testApplication {
                val config = TestConfig.default(fakes)
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation) {
                        jackson()
                    }
                }

                val journalpostId = UUID.randomUUID()
                val dokumentId = UUID.randomUUID()
                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/dokumenter") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }

                assertEquals(HttpStatusCode.OK, res.status)
            }
        }
    }
}