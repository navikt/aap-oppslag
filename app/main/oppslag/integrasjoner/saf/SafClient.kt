package oppslag.integrasjoner.saf

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.aap.ktor.client.TokenXProviderConfig
import no.nav.aap.ktor.client.TokenXTokenProvider
import oppslag.SECURE_LOGGER
import oppslag.SafConfig
import oppslag.http.HttpClientFactory

class SafClient(tokenXProviderConfig: TokenXProviderConfig, private val safConfig: SafConfig) {
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, safConfig.scope)
    private val httpClient = HttpClientFactory.create()

    suspend fun hentDokumenter(personident: String, tokenXToken: String, callId: String): List<Dokument> {
        val res = graphqlQuery(tokenXToken, SafRequest.hentDokumenter(personident), callId)
        val journalposter = res.data?.dokumentoversiktSelvbetjening
        return journalposter?.toDokumenter() ?: emptyList()
    }

    suspend fun hentDokument(tokenXToken: String, journalpostId: String, dokumentId: String, callId: String) =
        restQuery(tokenXToken, journalpostId, dokumentId, callId)

    private suspend fun graphqlQuery(tokenXToken: String, query: SafRequest, callId: String): SafRespons {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post(safConfig.baseUrl) {
            accept(ContentType.Application.Json)
            header("Nav-Callid", callId)
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(query)
        }
        val respons = request.body<SafRespons>()
        SECURE_LOGGER.info("Saf respons: ${respons}")
        if (respons.errors != null) {
            throw SafException("Feil mot SAF: ${respons.errors}")
        }
        return respons
    }

    private suspend fun restQuery(tokenXToken: String, journalpostId: String, dokumentId: String, callId: String) =
        httpClient.get("${safConfig.baseUrl}/rest/hentdokument/$journalpostId/$dokumentId/ARKIV") {
            header("Nav-Call-Id", callId)
            bearerAuth(tokenProvider.getOnBehalfOfToken(tokenXToken))
            contentType(ContentType.Application.Json)
        }.body<ByteArray>()
}