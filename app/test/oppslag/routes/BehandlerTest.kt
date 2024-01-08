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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BehandlerTest {
    @Test
    fun `Dummy test`() {
        Fakes().use { fakes ->
            testApplication {
                val config = TestConfig.default(fakes)
                application { api(config) }
                val client = createClient {
                    install(ContentNegotiation){
                        jackson()
                    } }

                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/behandler") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    accept(ContentType.Application.Json)
                }

                Assertions.assertEquals(HttpStatusCode.OK, res.status)
            }
        }
    }
}
