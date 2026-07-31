package oppslag.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.aap.komponenter.httpklient.httpclient.tokenprovider.OidcToken
import no.nav.aap.komponenter.server.auth.IdentityProvider
import oppslag.getEnvVar

internal class TexasGateway(
    private val identityProvider: IdentityProvider,
    private val httpClient: HttpClient,
) : ITokenProvider {
    private val texasTokenEndpoint by lazy { getEnvVar("NAIS_TOKEN_ENDPOINT") }
    private val texasExchangeEndpoint by lazy { getEnvVar("NAIS_TOKEN_EXCHANGE_ENDPOINT") }

    override suspend fun m2mToken(scope: String): String {
        return httpClient.post(texasTokenEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("identity_provider" to identityProvider.value, "target" to scope))
        }.body<Map<String, String>>()["access_token"]
            ?: error("Feil ved henting av M2M-token: mangler access_token i respons (identityProvider=$identityProvider)")
    }

    override suspend fun oboToken(scope: String, currentToken: OidcToken): String {
        return httpClient.post(texasExchangeEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "identity_provider" to identityProvider.value,
                    "target" to scope,
                    "user_token" to currentToken.token()
                )
            )
        }.body<Map<String, String>>()["access_token"]
            ?: error("Feil ved henting av OBO-token: mangler access_token i respons (identityProvider=$identityProvider)")
    }
}
