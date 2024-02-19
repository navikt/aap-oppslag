package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.SECURE_LOGGER
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
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Fant ikke person")
                        }
                    }
                    .onFailure {
                        call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
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
                        call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                    }
            }
        }
    }
}
