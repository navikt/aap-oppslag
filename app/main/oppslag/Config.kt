package oppslag

import no.nav.aap.ktor.client.AzureConfig
import no.nav.aap.ktor.client.TokenXProviderConfig
import java.net.URI

private fun getEnvVar(envar: String) = System.getenv(envar) ?: error("missing envvar $envar")

data class Config(
    val tokenx: TokenXProviderConfig = TokenXProviderConfig(
        clientId = getEnvVar("TOKEN_X_CLIENT_ID"),
        privateKey = getEnvVar("TOKEN_X_PRIVATE_JWK"),
        tokenEndpoint = getEnvVar("TOKEN_X_TOKEN_ENDPOINT"),
        jwksUrl = URI.create(getEnvVar("TOKEN_X_JWKS_URI")).toURL(),
        issuer = getEnvVar("TOKEN_X_ISSUER"),
    ),
    val azureConfig: AzureConfig= AzureConfig(
        clientId = getEnvVar("AZURE_APP_CLIENT_ID"),
        clientSecret = getEnvVar("AZURE_APP_CLIENT_SECRET"),
        tokenEndpoint = URI.create(getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT")).toURL(),
    ),
    val pdlConfig: PdlConfig = PdlConfig(),
    val krrConfig: KrrConfig = KrrConfig(),
    val behandlerConfig: BehandlerConfig = BehandlerConfig(),
    val safConfig: SafConfig = SafConfig()
)


data class PdlConfig(
    val baseUrl: String = getEnvVar("PDL_BASE_URL"),
    val audience: String = getEnvVar("PDL_AUDIENCE"),
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

data class SafConfig(
    val baseUrl: String = getEnvVar("SAF_BASE_URL"),
    val scope: String = getEnvVar("SAF_SCOPE")
)
