package oppslag.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.saf.SafClient

fun Route.safRoute(saf: SafClient) {
    route("/dokumenter") {
        get {
            val personIdent = call.personident()
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            call.respond(saf.hentDokumenter(personIdent, call.authToken(), callId))
        }
    }
}