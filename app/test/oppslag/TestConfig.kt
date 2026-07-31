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
                audience = "dev-fss.teamdokumenthandtering.krr"
            ),
            behandlerConfig = BehandlerConfig(
                baseUrl = "http://localhost:${Fakes.behandler.port()}",
                audience = "dev-fss.teamdokumenthandtering.behandler"
            ),
            safConfig = SafConfig(
                baseUrl = "http://localhost:${Fakes.saf.port()}",
                audience = "dev-fss.teamdokumenthandtering.safselvbetjening"
            )
        )
    }

}
