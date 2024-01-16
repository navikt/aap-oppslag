package oppslag.integrasjoner.krr

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.prometheus.client.Summary
import kotlinx.coroutines.runBlocking
import no.nav.aap.ktor.client.TokenXProviderConfig
import no.nav.aap.ktor.client.TokenXTokenProvider
import oppslag.KrrConfig
import oppslag.http.HttpClientFactory

private const val KRR_CLIENT_SECONDS_METRICNAME = "krr_client_seconds"
private val clientLatencyStats: Summary = Summary.build()
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
        callId: String?
    ): KrrRespons =
        clientLatencyStats.startTimer().use {
            runBlocking {
                val obotoken = tokenProvider.getOnBehalfOfToken(tokenXToken)
                val response = httpClient.get("${krrConfig.baseUrl}/rest/v1/person") {
                    accept(ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Personident", personIdent)
                    bearerAuth(obotoken)
                }
                if (response.status.isSuccess() || response.status.value == 409) {
                    response.body()
                } else {
                    error("Feil mot krr (${response.status}): ${response.bodyAsText()}")
                }
            }
        }
}
