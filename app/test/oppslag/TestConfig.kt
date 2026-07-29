package oppslag

internal object TestConfig {
    fun default(fakes: Fakes): Config {
        return Config(
            pdlConfig = PdlConfig(
                baseUrl = "http://localhost:${fakes.pdl.port()}",
                audience = "dev-fss:pdl:pdl-api",
                scope = "api://dev-fss.pdl:pdl-api/.default"
            ),
            krrConfig = KrrConfig(
                baseUrl = "http://localhost:${fakes.krr.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.krr/.default"
            ),
            behandlerConfig = BehandlerConfig(
                baseUrl = "http://localhost:${fakes.behandler.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.behandler/.default"
            ),
            safConfig = SafConfig(
                baseUrl = "http://localhost:${fakes.saf.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.safselvbetjening/.default"
            )
        )
    }

}
