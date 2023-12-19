package oppslag

import no.nav.aap.ktor.client.TokenXProviderConfig
import java.net.URI

private fun getEnvVar(envar: String) = System.getenv(envar) ?: error("missing envvar $envar")

data class Config(
    val tokenx: TokenXProviderConfig = TokenXProviderConfig(
        clientId = getEnvVar("TOKEN_X_CLIENT_ID"),
        privateKey = getEnvVar("TOKEN_X_PRIVATE_KEY"),
        tokenEndpoint = getEnvVar("TOKEN_X_TOKEN_ENDPOINT"),
        jwksUrl = URI(getEnvVar("TOKEN_X_WELL_KNOWN_URL")).toURL(),
        issuer = getEnvVar("TOKEN_X_ISSUER"),
    ),
    val PdlConfig: PdlConfig = PdlConfig()
    )


data class PdlConfig(
    val baseUrl: String = getEnvVar("PDL_BASE_URL"),
    val scope: String = getEnvVar("PDL_SCOPE")
)

data class KrrConfig(
    val baseUrl: String = getEnvVar("KRR_BASE_URL"),
    val scope: String = getEnvVar("KRR_SCOPE")
)

data class BehandlerConfig(
    val baseUrl: String = getEnvVar("BEHANDLER_BASE_URL"),
    val scope: String = getEnvVar("BEHANDLER_SCOPE")
)

data class JoarkConfig(
    val baseUrl: String = getEnvVar("JOARK_BASE_URL"),
    val scope: String = getEnvVar("JOARK_SCOPE")
)
