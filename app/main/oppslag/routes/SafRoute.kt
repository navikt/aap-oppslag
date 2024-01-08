package oppslag.routes

import io.ktor.http.*
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

        get("/{journalpostid}/{dokumentid}") {
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            val journalpostid = requireNotNull(call.parameters["journalpostid"]) { "journalpostid er ikke satt" }
            val dokumentid = requireNotNull(call.parameters["dokumentid"]) { "dokumentid er ikke satt" }
            val fil = saf.hentDokument(call.authToken(), journalpostid, dokumentid, callId)

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(
                        ContentDisposition.Parameters.FileName,
                        "${dokumentid}.pdf"
                    )
                    .toString()
            )
            call.respond(HttpStatusCode.OK, fil)
        }
    }
}