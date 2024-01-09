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
        get("api/person/v1/behandler/self"){
            call.respond(
                listOf(
                    BehandlerRespons(
                        type = "FASTLEGE", 
                        kategori = "aap", 
                        behandlerRef = "12", 
                        fnr=null, 
                        fornavn = "fornavn", 
                        mellomnavn = "mellomnavn", 
                        etternavn = "etternavn", 
                        orgnummer = "orgnummer", 
                        kontor = "kontor", 
                        adresse = null, 
                        postnummer = "postnummer", 
                        poststed = "poststed", 
                        telefon = "telefon"
                    )
                )
            )
        }
    }
}
