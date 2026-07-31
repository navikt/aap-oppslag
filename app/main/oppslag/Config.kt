package oppslag

fun getEnvVar(envar: String) = System.getenv(envar) ?: System.getProperty(envar) ?: error("missing envvar $envar")

data class Config(
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
    val audience: String = getEnvVar("KRR_AUDIENCE")
)

data class BehandlerConfig(
    val baseUrl: String = getEnvVar("BEHANDLER_BASE_URL"),
    val audience: String = getEnvVar("BEHANDLER_AUDIENCE")
)

data class SafConfig(
    val baseUrl: String = getEnvVar("SAF_BASE_URL"),
    val audience: String = getEnvVar("SAF_AUDIENCE")
)
