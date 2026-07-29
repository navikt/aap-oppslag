package oppslag

fun getEnvVar(envar: String) = System.getProperty(envar) ?: System.getenv(envar) ?: error("missing envvar $envar")

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
