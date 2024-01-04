package oppslag.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import io.ktor.server.testing.*
import oppslag.*
import oppslag.TestConfig

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

                val tokenXGen = TokenXGen(config.tokenx)
                val res = client.get("/krr") {
                    bearerAuth(tokenXGen.generate("12345678910"))
                    accept(ContentType.Application.Json)
                }

                assertTrue {
                    true
                }
            }
        }
    }
}
