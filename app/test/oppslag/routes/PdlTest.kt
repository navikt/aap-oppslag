package oppslag.routes

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
import oppslag.integrasjoner.pdl.Barn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class PdlTest {

    @Test
    fun `Kan hente personData`() {
        Fakes().use { fakes ->
            testApplication {
                val config = TestConfig.default(fakes)
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation){
                        jackson()
                    } }

                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/person") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }

                assertEquals(HttpStatusCode.OK, res.status)
            }
        }
    }

    @Test
    fun `Kan hente levende og umyndige barn`() {
        Fakes().use { fakes ->
            testApplication {
                val config = TestConfig.default(fakes)
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation){
                        jackson {
                            registerModule(JavaTimeModule())
                        }
                    } }

                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/person/barn") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }

                val barn = res.body<List<Barn>>().single()
                assertEquals(barn.navn, "kari Mellomnavn Nordmann")
                assertEquals(HttpStatusCode.OK, res.status)

            }
        }
    }
}