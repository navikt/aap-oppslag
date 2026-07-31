package oppslag.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import no.nav.aap.komponenter.httpklient.httpclient.tokenprovider.OidcToken

internal fun ApplicationCall.personident(): String {
    return requireNotNull(principal<JWTPrincipal>()) {
        "principal mangler i ktor auth"
    }.getClaim("pid", String::class)
        ?: error("pid mangler i tokenx claims")
}

internal fun ApplicationCall.authToken(): OidcToken {
    return requireNotNull(this.request.headers["Authorization"]) {
        "Authorization header mangler"
    }.removePrefix("Bearer ")
        .let { OidcToken(it) }
}
