package oppslag.integrasjoner.pdl

import oppslag.integrasjoner.pdl.PdlRequest.Companion.hentBarnInfo
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.aap.ktor.client.TokenXProviderConfig
import no.nav.aap.ktor.client.TokenXTokenProvider
import oppslag.PdlConfig
import oppslag.http.HttpClientFactory

class PdlGraphQLClient(tokenXProviderConfig: TokenXProviderConfig, private val pdlConfig: PdlConfig) {
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, pdlConfig.audience)
    private val httpClient = HttpClientFactory.create()

    suspend fun hentPerson(personident: String, tokenXToken: String, callId: String): Result<Søker?> {
        val res = query(tokenXToken, PdlRequest.hentPerson(personident), callId)
        return res.map { it.data?.hentPerson?.toSøker() }
    }

    suspend fun hentBarn(personident: String, tokenXToken: String, callId: String): Result<List<Barn>> {
        val maybeRelatertPersonIdenter = hentBarnRelasjon(personident, tokenXToken, callId).map {
            it?.mapNotNull { it.relatertPersonsIdent }
                ?.toList()
                ?: emptyList()
        }
        val maybeBarn = runCatching {
            maybeRelatertPersonIdenter.map { personIdenter ->
                hentBarnBolk(tokenXToken, personIdenter, callId).map {
                    it.filter {
                        it.myndig().not() && it.beskyttet().not() && it.død().not()
                    }
                }
            }.fold(
                onSuccess = { it.getOrThrow() },
                onFailure = { throw it }
            )
        }
        return maybeBarn.map {
            mapToBarn(it)

        }
    }


    private suspend fun hentBarnRelasjon(
        personident: String,
        tokenXToken: String,
        callId: String
    ): Result<List<PdlForelderBarnRelasjon>?> =
        query(
            tokenXToken,
            PdlRequest.hentBarnRelasjon(personident),
            callId
        ).map { it.data?.hentPerson?.foreldreBarnRelasjon }

    private suspend fun hentBarnBolk(tokenXToken: String, list: List<String>, callId: String): Result<List<PdlPerson>> {
        return query(tokenXToken, hentBarnInfo(list), callId).map {
            it.data?.hentPersoner?.map { barnInfo ->
                PdlPerson(
                    adressebeskyttelse = barnInfo.adressebeskyttelse,
                    navn = barnInfo.navn,
                    foedsel = barnInfo.foedsel,
                    foreldreBarnRelasjon = null,
                    bostedsadresse = null,
                    fnr = barnInfo.fnr,
                    doedsfall = null,
                    code = barnInfo.code
                )
            } ?: emptyList()
        }
    }

    private suspend fun query(tokenXToken: String, query: PdlRequest, callId: String): Result<PdlResponse> {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post(pdlConfig.baseUrl) {
            accept(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("TEMA", "AAP")
            bearerAuth(token)
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

private fun filter(barn: List<PdlPerson>): List<PdlPerson> {
    return barn

}

private fun mapToBarn(barn: List<PdlPerson>): List<Barn> {
    return barn.map { it.toBarn() }
}