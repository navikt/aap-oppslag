package oppslag.fakes

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.TOKEN_X_JWKS

fun Application.AzureAdFake() {
    install(ContentNegotiation){
        jackson()
    }

    routing {
        post("/token") {
            call.respond(
                TestToken()
            )
        }
        get("/jwks") {
            call.respondText(TOKEN_X_JWKS)
        }
    }
}

