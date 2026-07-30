package oppslag.integrasjoner.saf

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.plugins.NotFoundException
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.InputStream
import no.nav.aap.komponenter.httpklient.httpclient.tokenprovider.OidcToken
import oppslag.LOGGER
import oppslag.SafConfig
import oppslag.auth.TokenXTokenProvider
import oppslag.http.HttpClientFactory

class SafClient(private val safConfig: SafConfig) {
    private val httpClient = HttpClientFactory.create()

    suspend fun hentJson(oidcToken: OidcToken, callId: String, journalpostId: String): ByteArray {
        val journalpostreq = graphqlQuery(oidcToken, SafRequest.hentJournalpost(journalpostId), callId)
        val journalpost = journalpostreq.data?.journalpostById?: throw SafException("Fant ikke journalpost for $journalpostId")

        val dokument = journalpost.dokumenter?.find {dokInfo ->
            dokInfo?.dokumentvarianter?.find { dokVariant ->
                dokVariant?.variantformat == SafVariantformat.ORIGINAL
            } != null
        }
        if(dokument == null) throw NotFoundException("Fant ikke original for journalpost $journalpostId").also {
            LOGGER.error("Fant ikke orginalJson for søknad med journalpost: $journalpostId")
            LOGGER.error("innhold i journalpost: $journalpostreq")
        }

        val response = restQuery(oidcToken, journalpostId, dokument.dokumentInfoId, callId, "ORIGINAL")

        return when(response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw DokumentIkkeFunnet("Fant ikke dokument ${dokument.dokumentInfoId} for journalpost $journalpostId")
            else -> throw SafException("Feil fra saf: ${response.status} : ${response.bodyAsText()}")
        }
    }

    suspend fun hentJournalpostSomDokumenter(journalpostId: String, oidcToken: OidcToken, callId: String): List<Dokument> {
        val res = graphqlQuery(oidcToken, SafRequest.hentJournalpost(journalpostId), callId)
        val journalposter = res.data?.journalpostById?.toDokumenter()
        return journalposter ?: emptyList()
    }

    suspend fun hentDokumenter(personident: String, oidcToken: OidcToken, callId: String): List<Dokument> {
        val res = graphqlQuery(oidcToken, SafRequest.hentDokumenter(personident), callId)
        val journalposter = res.data?.dokumentoversiktSelvbetjening?.toDokumenter()
        return journalposter?: emptyList()
    }

    suspend fun hentDokument(oidcToken: OidcToken, journalpostId: String, dokumentId: String, callId: String): InputStream {
        val response = restQuery(oidcToken, journalpostId, dokumentId, callId)

        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsChannel().toInputStream()
            HttpStatusCode.NotFound -> throw DokumentIkkeFunnet("Fant ikke dokument $dokumentId for journalpost $journalpostId")
            else -> throw SafException("Feil fra saf: ${response.status} : ${response.bodyAsText()}")
        }
    }

    private suspend fun graphqlQuery(oidcToken: OidcToken, query: SafRequest, callId: String): SafRespons {
        val token = TokenXTokenProvider.oboToken(safConfig.audience, oidcToken)
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

    private suspend fun restQuery(
        oidcToken: OidcToken,
        journalpostId: String,
        dokumentId: String,
        callId: String,
        arkivtype: String = "ARKIV"
    ) =
        httpClient.get("${safConfig.baseUrl}/rest/hentdokument/$journalpostId/$dokumentId/$arkivtype") {
            header("Nav-Call-Id", callId)
            bearerAuth(TokenXTokenProvider.oboToken(safConfig.audience, oidcToken))
            contentType(ContentType.Application.Json)
        }
}