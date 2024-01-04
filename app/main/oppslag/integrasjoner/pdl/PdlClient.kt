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

    suspend fun hentPerson(personident: String, tokenXToken: String, callId: String): Søker? {
        val res = query(tokenXToken, PdlRequest.hentPerson(personident), callId)
        val person = res.data?.hentPdlPerson
        return person?.toSøker()
    }

    suspend fun hentBarn(personident: String, tokenXToken: String, callId: String): List<Barn> {
        val barnRelasjon: List<String> = hentBarnRelasjon(personident, tokenXToken, callId)
            ?.mapNotNull { it.relatertPersonsIdent }
            ?.toList()
            ?: emptyList()
        return mapToBarn(filter(hentBarn(tokenXToken, barnRelasjon, callId)))

    }

    private suspend fun hentBarnRelasjon(personident: String, tokenXToken: String, callId: String) =
        query(tokenXToken, PdlRequest.hentBarnRelasjon(personident), callId).data?.hentPdlPerson?.foreldreBarnRelasjon

    private suspend fun hentBarn(tokenXToken: String, list: List<String>, callId: String): List<PdlPerson> {
        return list.map { fnr ->
            val barnInfo = query(tokenXToken, hentBarnInfo(fnr), callId).data?.hentPdlPerson
            PdlPerson(
                adressebeskyttelse = barnInfo?.adressebeskyttelse,
                navn = barnInfo?.navn,
                foedsel = barnInfo?.foedsel,
                foreldreBarnRelasjon = null,
                bostedsadresse = null,
                fnr = fnr,
                doedsfall = null
            )
        }.toList()
    }

    private suspend fun query(tokenXToken: String, query: PdlRequest, callId: String): PdlResponse {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post(pdlConfig.baseUrl) {
            accept(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("TEMA", "AAP")
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(query)
        }
        val respons = request.body<PdlResponse>()
        if (respons.errors != null) {
            throw PdlException("Feil mot PDL: ${respons.errors}")
        }
        return request.body()
    }
}

private fun filter(barn:List<PdlPerson>): List<PdlPerson>{
    return barn
        .filter { it.myndig().not() }
        .filter { it.beskyttet().not() }
        .filter { it.død().not() }
}

private fun mapToBarn(barn: List<PdlPerson>): List<Barn> {
    return barn.map { it.toBarn() }
}