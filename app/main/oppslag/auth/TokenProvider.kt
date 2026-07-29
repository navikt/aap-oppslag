package oppslag.auth

import no.nav.aap.komponenter.httpklient.httpclient.tokenprovider.OidcToken
import no.nav.aap.komponenter.server.auth.IdentityProvider
import oppslag.http.HttpClientFactory

interface ITokenProvider {
    suspend fun m2mToken(scope: String): String
    suspend fun oboToken(scope: String, currentToken: OidcToken): String
}

private val texasHttpClient = HttpClientFactory.create()

object TokenXTokenProvider : ITokenProvider {
    private val texasGateway: TexasGateway = TexasGateway(IdentityProvider.TOKENX, texasHttpClient)

    override suspend fun oboToken(scope: String, currentToken: OidcToken): String {
        return texasGateway.oboToken(scope, currentToken)
    }

    override suspend fun m2mToken(scope: String): String {
        return texasGateway.m2mToken(scope)
    }
}

object AzureTokenProvider : ITokenProvider {
    private val texasGateway: TexasGateway = TexasGateway(IdentityProvider.ENTRA_ID, texasHttpClient)

    override suspend fun oboToken(scope: String, currentToken: OidcToken): String {
        return texasGateway.oboToken(scope, currentToken)
    }

    override suspend fun m2mToken(scope: String): String {
        return texasGateway.m2mToken(scope)
    }
}