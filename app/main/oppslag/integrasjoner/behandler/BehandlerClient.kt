package oppslag.integrasjoner.behandler

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.prometheus.metrics.core.metrics.Summary
import kotlinx.coroutines.runBlocking
import oppslag.BehandlerConfig
import oppslag.auth.TokenXProviderConfig
import oppslag.auth.TokenXTokenProvider
import oppslag.http.HttpClientFactory

private const val BEHANDLER_CLIENT_SECONDS_METRICNAME = "behandler_client_seconds"
private val clientLatencyStats = Summary.builder()
    .name(BEHANDLER_CLIENT_SECONDS_METRICNAME)
    .quantile(0.5, 0.05) // Add 50th percentile (= median) with 5% tolerated error
    .quantile(0.9, 0.01) // Add 90th percentile with 1% tolerated error
    .quantile(0.99, 0.001) // Add 99th percentile with 0.1% tolerated error
    .help("Latency behandler, in seconds")
    .register()

class BehandlerClient(tokenXProviderConfig: TokenXProviderConfig, private val behandlerConfig: BehandlerConfig) {
    private val httpClient = HttpClientFactory.create()
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, behandlerConfig.scope, httpClient)

    fun hentBehandler(
        tokenXToken:String,
        callId: String?
    ): List<BehandlerRespons> =
        clientLatencyStats.startTimer().use {
            runBlocking {
                val obotoken = tokenProvider.getOnBehalfOfToken(tokenXToken)
                val response = httpClient.get("${behandlerConfig.baseUrl}/api/person/v1/behandler/self") {
                    accept(ContentType.Application.Json)
                    header("Nav-Callid", callId) //TODO: sjekk om dette er riktig
                    bearerAuth(obotoken)
                }
                if (response.status.isSuccess() || response.status.value == 409) {
                    response.body()
                } else {
                    error("Feil mot behandler (${response.status}): ${response.bodyAsText()}")
                }
            }
        }
}

