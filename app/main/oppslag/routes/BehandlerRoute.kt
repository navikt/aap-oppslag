package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.integrasjoner.behandler.BehandlerClient
import java.util.UUID

fun Route.behandlerRoute(behandler: BehandlerClient) {
    route("/behandler") {
        get {
            val callId = call.request.header("Nav-CallId")?:UUID.randomUUID().toString()
            val behandlersvar = behandler.hentBehandler(call.authToken(), callId).tilBehandler()
            call.respond(HttpStatusCode.OK, behandlersvar)
        }
    }
}
