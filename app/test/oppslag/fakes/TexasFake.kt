package oppslag.fakes

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
    val access_token: String = "very.secure.token",
    val exprires_in: Int = 3599,
)

