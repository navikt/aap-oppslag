package oppslag.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID
import no.nav.aap.komponenter.server.auth.IdentityProvider
import oppslag.LOGGER
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.pdl.PdlGraphQLClient

fun Route.pdlRoute(pdl: PdlGraphQLClient) {
    authenticate(IdentityProvider.TOKENX.value) {
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
                        LOGGER.error("Feil ved henting av person", it)
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
                    LOGGER.error("Feil ved henting av barn", it)
                    call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                }
            }
        }
    }

    authenticate(IdentityProvider.ENTRA_ID.value) {
        route("/person") {
            get("/navn") {
                val callId = call.request.header("Nav-CallId") ?: UUID.randomUUID().toString()
                val personident = call.request.header("personident")
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "mangler personident i header")

                pdl.hentNavn(personident, callId)
                    .onSuccess { call.respond(it) }
                    .onFailure {
                        LOGGER.error("Feil ved henting av navn", it)
                        call.respond(HttpStatusCode.InternalServerError, "Feil ved oppslag i PDL: ${it.message}")
                    }
            }
        }
    }
}
