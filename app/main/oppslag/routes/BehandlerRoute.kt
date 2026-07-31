package oppslag.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID
import oppslag.LOGGER
import oppslag.auth.authToken
import oppslag.integrasjoner.behandler.BehandlerClient
import oppslag.integrasjoner.behandler.RegistrertBehandler

fun Route.behandlerRoute(behandler: BehandlerClient) {
    route("/fastlege") {
        get {
            val callId = call.request.header("Nav-CallId")?:UUID.randomUUID().toString()
            val behandlersvar = behandler.hentBehandler(call.authToken(), callId)
                .filter { RegistrertBehandler.Type.valueOf(it.type) == RegistrertBehandler.Type.FASTLEGE }
                .map { it.tilBehandler() }

            if (behandlersvar.size > 1) {
                LOGGER.warn("Fant flere fastleger for bruker")
            }

            call.respond(HttpStatusCode.OK, behandlersvar)
        }
    }
}

