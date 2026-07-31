package oppslag.routes

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import java.util.UUID
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.WithFakes
import oppslag.api
import oppslag.integrasjoner.pdl.Barn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@WithFakes
class PdlTest {

    @Test
    fun `Kan hente personData`() {
            testApplication {
                val config = TestConfig.default()
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation){
                        jackson()
                    } }

                val res = client.get("/person") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }

                assertEquals(HttpStatusCode.OK, res.status)
            }
    }

    @Test
    fun `Kan hente levende og umyndige barn`() {
            testApplication {
                val config = TestConfig.default()
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation){
                        jackson {
                            registerModule(JavaTimeModule())
                        }
                    } }

                val res = client.get("/person/barn") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    header("Nav-CallId", UUID.randomUUID())
                    accept(ContentType.Application.Json)
                }

                val barn = res.body<List<Barn>>().single()
                assertEquals(barn.navn, "kari Mellomnavn Nordmann")
                assertEquals(HttpStatusCode.OK, res.status)
            }
    }
}
