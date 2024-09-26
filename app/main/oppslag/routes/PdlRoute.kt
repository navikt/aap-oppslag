package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.SECURE_LOGGER
import oppslag.LOGGER
import oppslag.auth.AZURE
import oppslag.auth.TOKENX
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.pdl.PdlGraphQLClient
import java.util.*

fun Route.pdlRoute(pdl: PdlGraphQLClient) {
    authenticate(TOKENX) {
        route("/person") {
            get {
                val personIdent = call.personident()
                val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
                pdl.hentPerson(personIdent, call.authToken(), callId)
                    .onSuccess {
                        if (it != null) {
                            call.respond(HttpStatusCode.OK, it)
                            LOGGER.trace("Hentet person")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Fant ikke person")
                            LOGGER.warn("Fant ikke person")
                        }
                    }
                    .onFailure {
                        call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                        LOGGER.error("Feil ved henting av person, sjekk securelogs for mer info")
                        SECURE_LOGGER.error("Feil ved henting av person", it)
                    }
            }
            get("/barn") {
                val personIdent = call.personident()
                val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
                val barn = pdl.hentBarn(personIdent, call.authToken(), callId)
                barn.onSuccess {
                    call.respond(HttpStatusCode.OK, it)
                }
                barn.onFailure {
                    SECURE_LOGGER.error("Feil ved henting av barn", it)
                    LOGGER.error("Feil ved henting av barn, sjekk securelogs for mer info")
                    call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                }
            }
        }
    }

    authenticate(AZURE) {
        route("/person") {
            get("/navn") {
                val callId = call.request.header("Nav-CallId") ?: UUID.randomUUID().toString()
                val personident = call.request.header("personident")
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "mangler personident i header")

                pdl.hentNavn(personident, callId)
                    .onSuccess { call.respond(it) }
                    .onFailure {
                        SECURE_LOGGER.error("Feil ved henting av navn", it)
                        LOGGER.error("Feil ved henting av navn. Sjekk secure log for stacktrace")
                        call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                    }
            }
        }
    }
}
