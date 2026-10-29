package oppslag.routes

import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID
import oppslag.auth.authToken
import oppslag.auth.personident
import oppslag.integrasjoner.krr.Kontaktinformasjon
import oppslag.integrasjoner.krr.KrrClient
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("KrrRoute")

fun Route.krrRoute(krr: KrrClient) {
    route("/krr") {
        get {
            val personIdent = requireNotNull(call.personident())
            val callId = call.request.header("Nav-CallId") ?: UUID.randomUUID().toString()

            val respons = krr.hentKontaktinformasjon(call.authToken(), personIdent, callId)

            val kontaktinformasjon = respons.personer?.get(personIdent)?.tilKontaktinfo()

            if (kontaktinformasjon != null) {
                logger.trace("Hentet kontaktinformasjon for bruker")

                call.respond(kontaktinformasjon)
            } else {
                when (val feilmelding = respons.feil!![personIdent]) {
                    "person_ikke_funnet" -> logger.warn("Fant ikke kontaktinformasjon for bruker")
                    else -> logger.error("Fikk feilmelding fra KRR: $feilmelding")
                }

                call.respond(Kontaktinformasjon())
            }
        }
    }
}
