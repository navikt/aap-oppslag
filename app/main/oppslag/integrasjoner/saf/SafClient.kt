package oppslag.integrasjoner.saf

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import oppslag.auth.TokenXProviderConfig
import oppslag.auth.TokenXTokenProvider
import oppslag.SafConfig
import oppslag.http.HttpClientFactory

class SafClient(tokenXProviderConfig: TokenXProviderConfig, private val safConfig: SafConfig) {
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, safConfig.scope)
    private val httpClient = HttpClientFactory.create()

    suspend fun hentJson(personident: String, tokenXToken: String, callId: String, journalpostId: String): ByteArray {
        val res = graphqlQuery(tokenXToken, SafRequest.hentDokumenter(personident), callId)
        val journalpost = res.data?.dokumentoversiktSelvbetjening?.journalposter?.find {
            it?.journalpostId == journalpostId
        } ?: throw SafException("Fant ikke journalpost $journalpostId") //todo: erstatt men nytt safoppslag når tilgjengelig

        val dokument = journalpost.dokumenter?.find {dokInfo ->
            dokInfo?.dokumentvarianter?.find { dokVariant ->
                dokVariant?.variantformat == SafVariantformat.ORIGINAL
            } != null
        }

        return hentDokument(tokenXToken = tokenXToken, journalpostId = journalpostId, dokumentId = dokument?.dokumentInfoId!!, callId = callId)

    }

    suspend fun hentDokumenter(personident: String, tokenXToken: String, callId: String): List<Dokument> {
        val res = graphqlQuery(tokenXToken, SafRequest.hentDokumenter(personident), callId)
        val journalposter = res.data?.dokumentoversiktSelvbetjening?.toDokumenter()
        return journalposter?: emptyList()
    }

    suspend fun hentDokument(tokenXToken: String, journalpostId: String, dokumentId: String, callId: String): ByteArray {
        val response = restQuery(tokenXToken, journalpostId, dokumentId, callId)

        return when(response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw DokumentIkkeFunnet("Fant ikke dokument $dokumentId for journalpost $journalpostId")
            else -> throw SafException("Feil fra saf: ${response.status} : ${response.bodyAsText()}")
        }
    }

    private suspend fun graphqlQuery(tokenXToken: String, query: SafRequest, callId: String): SafRespons {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post("${safConfig.baseUrl}/graphql") {
            accept(ContentType.Application.Json)
            header("Nav-Callid", callId)
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(query)
        }

        val respons = request.body<SafRespons>()
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
        }
}