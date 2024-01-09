package oppslag.fakes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.integrasjoner.behandler.BehandlerRespons

fun Application.BehandlerFake() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        get("/rest/v1/person"){
            call.respond(BehandlerRespons("type", "aap", "12", fnr=null, "fornavn", "mellomnavn", "etternavn", "orgnummer", "kontor", null, "postnummer", "poststed", "telefon"))
        }
    }
}