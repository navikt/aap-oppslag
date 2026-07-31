package oppslag.fakes

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import oppslag.TokenXGen

fun Application.texasFake() {
    install(ContentNegotiation) {
        jackson()
    }

    routing {
        post("/token") {
            call.respond(TestToken(TokenXGen.generate("12345678910")))
        }
        post("/token/exchange") {
            call.respond(TestToken(TokenXGen.generate("123456789")))
        }
        post("/introspect") {
            call.respond(mapOf("active" to true))
        }
    }
}

data class TestToken(
    @param:JsonProperty("access_token") val accessToken: String = "very.secure.token",
    @param:JsonProperty("expires_in") val expiresIn: Long = 3600,
)
