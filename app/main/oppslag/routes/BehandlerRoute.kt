package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.authToken
import oppslag.integrasjoner.behandler.BehandlerClient
import oppslag.integrasjoner.behandler.BehandlerRespons
import oppslag.integrasjoner.behandler.RegistrertBehandler
import java.util.UUID
import oppslag.SECURE_LOGGER

fun Route.behandlerRoute(behandler: BehandlerClient) {
    route("/fastlege") {
        get {
            val callId = call.request.header("Nav-CallId")?:UUID.randomUUID().toString()
            val behandlersvar = behandler.hentBehandler(call.authToken(), callId)
                .map { it.tilBehandler() }
                .filter { it.type == RegistrertBehandler.Type.FASTLEGE }

            if (behandlersvar.size > 1) {
                SECURE_LOGGER.warn("Dette var rart, fant fler fastleger for bruker :wtf:")
            }

            behandlersvar.firstOrNull()
                ?.let { call.respond(HttpStatusCode.OK, it)} 
                ?: call.respond(HttpStatusCode.NotFound, "Fant ikke fastlege")

            call.respond(HttpStatusCode.OK, behandlersvar)
        }
    }
}

