package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.integrasjoner.krr.KontaktinformasjonClient

fun Route.krrRoute(krr: KontaktinformasjonClient) {
    route("/krr") {
        get {
            val personIdent = requireNotNull(call.parameters["personIdent"])
            call.respond(HttpStatusCode.OK, krr.hentKrr(call.authentication.toString(), personIdent))
        }
    }
}