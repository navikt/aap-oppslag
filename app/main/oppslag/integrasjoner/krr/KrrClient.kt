package oppslag.integrasjoner.krr

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.prometheus.metrics.core.metrics.Summary
import kotlinx.coroutines.runBlocking
import oppslag.KrrConfig
import oppslag.auth.TokenXProviderConfig
import oppslag.auth.TokenXTokenProvider
import oppslag.http.HttpClientFactory

private const val KRR_CLIENT_SECONDS_METRICNAME = "krr_client_seconds"
private val clientLatencyStats: Summary = Summary.builder()
    .name(KRR_CLIENT_SECONDS_METRICNAME)
    .quantile(0.5, 0.05) // Add 50th percentile (= median) with 5% tolerated error
    .quantile(0.9, 0.01) // Add 90th percentile with 1% tolerated error
    .quantile(0.99, 0.001) // Add 99th percentile with 0.1% tolerated error
    .help("Latency krr, in seconds")
    .register()

class KrrClient(tokenXProviderConfig: TokenXProviderConfig, private val krrConfig: KrrConfig) {
    private val httpClient = HttpClientFactory.create()
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, krrConfig.scope, httpClient)

    fun hentKontaktinformasjon(
        tokenXToken: String,
        personIdent: String,
        callId: String?,
    ): KrrRespons =
        clientLatencyStats.startTimer().use {
            runBlocking {
                val obotoken = tokenProvider.getOnBehalfOfToken(tokenXToken)
                val response = httpClient.post("${krrConfig.baseUrl}/rest/v1/personer") {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    bearerAuth(obotoken)
                    setBody(KrrRequest(listOf(personIdent)))
                }

                if (response.status.isSuccess()) {
                    response.body()
                } else {
                    error("Feil mot krr (${response.status}): ${response.bodyAsText()}")
                }
            }
        }
}

internal data class KrrRequest(
    val personidenter: List<String>,
)
