package oppslag.routes

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import oppslag.TestConfig
import oppslag.TokenXGen
import oppslag.WithFakes
import oppslag.api
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@WithFakes
class BehandlerTest {
    @Test
    fun `Dummy test`() {
            testApplication {
                val config = TestConfig.default()
                
                application { 
                    api(config) 
                }

                val client = createClient {
                    install(ContentNegotiation){
                        jackson()
                    } 
                }

                val res = client.get("/fastlege") {
                    bearerAuth(TokenXGen.generate("12345678910"))
                    accept(ContentType.Application.Json)
                }

                Assertions.assertEquals(HttpStatusCode.OK, res.status)
        }
    }
}
