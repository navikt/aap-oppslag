package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.krr.KrrClient
import java.util.*

fun Route.krrRoute(krr: KrrClient) {
    route("/krr") {
        get {
            val personIdent = requireNotNull(call.personident())
            val callId = call.request.header("Nav-CallId") ?: UUID.randomUUID().toString()
            try {
                val kontaktinformasjon = krr.hentKontaktinformasjon(call.authToken(), personIdent, callId).tilKontaktinfo()
                call.respond(HttpStatusCode.OK, kontaktinformasjon)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
