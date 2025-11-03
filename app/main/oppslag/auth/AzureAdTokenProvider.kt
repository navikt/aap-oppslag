package oppslag.auth

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

class AzureAdTokenProvider(
    private val config: AzureConfig,
    private val scope: String,
    private val client: HttpClient = defaultHttpClient,
) {

    suspend fun getClientCredentialToken() = getAccessToken(scope) {
        "client_id=${config.clientId}&client_secret=${config.clientSecret}&scope=$scope&grant_type=client_credentials"
    }

    private val cache = TokenCache()

    private suspend fun getAccessToken(cacheKey: String, body: () -> String): String {
        val token = cache.get(cacheKey)
            ?: client.post(config.tokenEndpoint) {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(body())
            }.also {
                if (!it.status.isSuccess()) {
                    secureLog.warn("Feilet token-kall {}: {}", it.status.value, it.bodyAsText())
                }
            }.body<Token>().also {
                cache.add(cacheKey, it)
            }

        return token.access_token
    }

    private companion object {
        private val defaultHttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                }
            }
        }
    }
}
