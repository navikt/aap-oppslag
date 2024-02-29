package oppslag.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
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

        get("/{journalpostid}") {
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            val journalpostid = requireNotNull(call.parameters["journalpostid"]) { "journalpostid er ikke satt" }

            call.respond(saf.hentJournalpostSomDokumenter(journalpostid, call.authToken(), callId))
        }

        get("/{journalpostid}/{dokumentid}") {
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            val journalpostid = requireNotNull(call.parameters["journalpostid"]) { "journalpostid er ikke satt" }
            val dokumentid = requireNotNull(call.parameters["dokumentid"]) { "dokumentid er ikke satt" }
            val filStream = saf.hentDokument(call.authToken(), journalpostid, dokumentid, callId)

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(
                        ContentDisposition.Parameters.FileName,
                        "${dokumentid}.pdf"
                    )
                    .toString()
            )
            call.respondOutputStream {
                filStream.copyTo(this)
            }
        }

        get("/{journalpostid}/json"){
            val token = call.authToken()
            val callId = requireNotNull(call.request.header("Nav-CallId")) { "x-callid ikke satt" }
            val journalpostid = requireNotNull(call.parameters["journalpostid"]) { "journalpostid er ikke satt" }

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(
                        ContentDisposition.Parameters.FileName,
                        "soknad.json"
                    )
                    .toString()
            )
            try {
                call.respond(
                    HttpStatusCode.OK,
                    saf.hentJson(
                        tokenXToken = token,
                        callId = callId,
                        journalpostId = journalpostid
                    )
                )
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}