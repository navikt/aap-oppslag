package oppslag.fakes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import oppslag.integrasjoner.krr.KrrPerson
import oppslag.integrasjoner.krr.KrrRespons

fun Application.KrrFake() {

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        post("/rest/v1/personer") {
            call.respond(
                KrrRespons(
                    personer = mapOf(
                        "12345678910" to KrrPerson(
                            kanVarsles = true,
                            aktiv = true,
                            epostadresse = "mail@mail.mail",
                            mobiltelefonnummer = "12345678"
                        )
                    )
                )
            )
        }
    }
}