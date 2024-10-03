package oppslag.integrasjoner.pdl

import oppslag.integrasjoner.pdl.PdlRequest.Companion.hentBarnInfo
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import oppslag.PdlConfig
import oppslag.SECURE_LOGGER
import oppslag.auth.AzureAdTokenProvider
import oppslag.auth.AzureConfig
import oppslag.auth.TokenXProviderConfig
import oppslag.auth.TokenXTokenProvider
import oppslag.http.HttpClientFactory

class PdlGraphQLClient(
    tokenXProviderConfig: TokenXProviderConfig,
    azureConfig: AzureConfig,
    private val pdlConfig: PdlConfig
) {
    private val httpClient = HttpClientFactory.create()
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, pdlConfig.audience)
    private val azureTokenProvider = AzureAdTokenProvider(
        azureConfig,
        pdlConfig.scope
    ).also { SECURE_LOGGER.info("azure scope: ${pdlConfig.scope}") }

    suspend fun hentPerson(personident: String, tokenXToken: String, callId: String): Result<Søker?> {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val res = query(token, PdlRequest.hentPerson(personident), callId)
        return res.map { it.data?.hentPerson?.toSøker() }
    }

    suspend fun hentNavn(personident: String, callId: String): Result<Navn> {
        val token = azureTokenProvider.getClientCredentialToken()

        return query(token, PdlRequest.hentNavn(personident), callId).map {
            it.data?.hentPerson?.toNavn() ?: error("Fant ikke person i PDL")
        }
    }

    suspend fun hentBarn(personident: String, tokenXToken: String, callId: String): Result<List<Barn>> {
        val maybeRelatertPersonIdenter = hentBarnRelasjon(personident, tokenXToken, callId)
            .map {
                it ?: emptyList()
            }

        return maybeRelatertPersonIdenter.map { personIdenter ->
            if (personIdenter.isEmpty()) {
                return Result.success(emptyList())
            }
            val listeMedBarn = hentBarnBolk(personIdenter, callId).filtrerBortDødeOgMyndige()
            if (listeMedBarn.harBeskyttedePersoner()) {
                listeMedBarn.maskerNavn()
            } else {
                listeMedBarn
            }
        }.map { resultat ->
            resultat.mapToBarn()
        }
    }

    private suspend fun hentBarnRelasjon(
        personident: String,
        tokenXToken: String,
        callId: String
    ): Result<List<String>?> {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        return query(
            token,
            PdlRequest.hentBarnRelasjon(personident),
            callId
        ).map { it.data?.hentPerson?.forelderBarnRelasjon?.mapNotNull { rel -> rel.relatertPersonsIdent } }
    }

    private suspend fun hentBarnBolk(personIdenter: List<String>, callId: String): Result<List<PdlPerson>> {
        val azureToken = azureTokenProvider.getClientCredentialToken()
        return query(azureToken, hentBarnInfo(personIdenter), callId)
            .map {
                it.data?.hentPersonBolk?.mapNotNull { barnInfo ->
                    barnInfo.person?.let { barn ->
                        PdlPerson(
                            adressebeskyttelse = barn.adressebeskyttelse,
                            navn = barn.navn,
                            foedselsdato = barn.foedselsdato,
                            forelderBarnRelasjon = null,
                            bostedsadresse = null,
                            fnr = barn.fnr,
                            doedsfall = barn.doedsfall,
                            code = barn.code
                        )
                    }
                } ?: emptyList()
            }
    }

    private suspend fun query(accessToken: String, query: PdlRequest, callId: String): Result<PdlResponse> {
        val request = httpClient.post(pdlConfig.baseUrl) {
            accept(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("TEMA", "AAP")
            header("Behandlingsnummer","B287")
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(query)
        }
        return runCatching {
            val respons = request.body<PdlResponse>()
            if (respons.errors != null) {
                throw PdlException("Feil mot PDL: ${respons.errors}")
            }
            respons
        }
    }
}

private fun Result<List<PdlPerson>>.mapToBarn(): List<Barn> =
    this.fold(
        onSuccess = {
            it.map { pdlPerson ->
                pdlPerson.toBarn()
            }
        },
        onFailure = {
            throw it
        }
    )

private fun Result<List<PdlPerson>>.filtrerBortDødeOgMyndige() =
    this.map {
        it.filter { barn ->
            barn.myndig().not() && barn.død().not()
        }
    }

private fun Result<List<PdlPerson>>.maskerNavn() =
    this.map {
        it.mapIndexed { x, barn ->
            barn.copy(
                navn = listOf(
                    PdlNavn("Barn", "${x + 1}", null)
                )
            )
        }
    }

private fun Result<List<PdlPerson>>.harBeskyttedePersoner(): Boolean =
    this.map {
        it.any { barn ->
            barn.strengtFortroligAdresse()
        }
    }.getOrNull() == true