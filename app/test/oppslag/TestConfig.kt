package oppslag

internal object TestConfig {
    fun default(): Config {
        return Config(
            pdlConfig = PdlConfig(
                baseUrl = "http://localhost:${Fakes.pdl.port()}",
                audience = "dev-fss:pdl:pdl-api",
                scope = "api://dev-fss.pdl:pdl-api/.default"
            ),
            krrConfig = KrrConfig(
                baseUrl = "http://localhost:${Fakes.krr.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.krr/.default"
            ),
            behandlerConfig = BehandlerConfig(
                baseUrl = "http://localhost:${Fakes.behandler.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.behandler/.default"
            ),
            safConfig = SafConfig(
                baseUrl = "http://localhost:${Fakes.saf.port()}",
                scope = "api://dev-fss.teamdokumenthandtering.safselvbetjening/.default"
            )
        )
    }

}
