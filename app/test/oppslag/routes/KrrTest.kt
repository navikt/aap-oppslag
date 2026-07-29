package oppslag.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.junit.jupiter.api.Test
import io.ktor.server.testing.*
import oppslag.*
import oppslag.TestConfig
import org.junit.jupiter.api.Assertions.assertEquals

class KrrTest {
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

                val res = client.get("/krr") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    accept(ContentType.Application.Json)
                }

                assertEquals(HttpStatusCode.OK, res.status)
            }
        }
    }
}
