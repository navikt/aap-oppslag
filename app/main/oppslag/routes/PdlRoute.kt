package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oppslag.auth.personident
import oppslag.integrasjoner.pdl.PdlGraphQLClient

fun Route.pdlRoute(pdl: PdlGraphQLClient) {
    route("/pdl") {
        get("/person") {
            val personIdent = call.personident()
            val søker = pdl.hentPerson(personIdent, call.authentication.toString())
            if (søker != null) {
                call.respond(HttpStatusCode.OK, søker)
            } else {
                call.respond(HttpStatusCode.NotFound, "Fant ikke person")
            }
        }
        get("/barn") {
            val personIdent = call.personident()
            call.respond(HttpStatusCode.OK, pdl.hentBarn(personIdent, "12345678910"))
        }
    }
}