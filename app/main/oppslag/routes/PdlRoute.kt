package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.pdl.PdlGraphQLClient

fun Route.pdlRoute(pdl: PdlGraphQLClient) {
    route("/pdl") {
        get("/person") {
            val personIdent = call.personident()
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            val søker = pdl.hentPerson(personIdent, call.authToken(), callId)
            if (søker != null) {
                call.respond(HttpStatusCode.OK, søker)
            } else {
                call.respond(HttpStatusCode.NotFound, "Fant ikke person")
            }
        }
        get("/barn") {
            val personIdent = call.personident()
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            call.respond(HttpStatusCode.OK, pdl.hentBarn(personIdent, call.authToken(), callId))
        }
    }
}