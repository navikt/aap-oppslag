package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.krr.KrrClient

fun Route.krrRoute(krr: KrrClient) {
    route("/krr") {
        get {
            val personIdent = requireNotNull(call.personident())

            call.respond(HttpStatusCode.OK, {})
        }
    }
}
