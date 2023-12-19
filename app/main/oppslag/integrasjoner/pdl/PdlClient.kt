package oppslag.integrasjoner.pdl

import PdlRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.aap.ktor.client.TokenXProviderConfig
import no.nav.aap.ktor.client.TokenXTokenProvider
import oppslag.PdlConfig
import oppslag.SECURE_LOGGER
import oppslag.http.HttpClientFactory
import java.util.*

// PDL skal ha 2 endepunkter: NAVN and KONTAKTINFO + eget oppslag for barn

internal class PdlGraphQLClient(private val pdlConfig: PdlConfig, tokenXProviderConfig: TokenXProviderConfig) {
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, pdlConfig.scope)
    private val httpClient = HttpClientFactory.create()

    suspend fun hentPerson(personident: String,tokenXToken:String) = query(PdlRequest.hentPerson(personident),tokenXToken)

    private suspend fun query(query: PdlRequest, tokenXToken:String): PdlResponse {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post("DummyString") {
            accept(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("TEMA", "AAP")
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(query)
        }
        return request.body()
    }

    private val callId: String get() = UUID.randomUUID().toString().also { SECURE_LOGGER.info("calling pdl with call-id $it") }
}