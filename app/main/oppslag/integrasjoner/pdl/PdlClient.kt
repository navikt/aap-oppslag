package oppslag.integrasjoner.pdl

import PdlRequest
import PdlRequest.Companion.hentBarnInfo
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.aap.ktor.client.TokenXProviderConfig
import no.nav.aap.ktor.client.TokenXTokenProvider
import oppslag.PdlConfig
import oppslag.SECURE_LOGGER
import oppslag.http.HttpClientFactory
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.util.*

class PdlGraphQLClient(private val pdlConfig: PdlConfig, tokenXProviderConfig: TokenXProviderConfig) {
    private val tokenProvider = TokenXTokenProvider(tokenXProviderConfig, pdlConfig.scope)
    private val httpClient = HttpClientFactory.create()

    suspend fun hentPerson(personident: String, tokenXToken:String):Søker? {
        val res = query(PdlRequest.hentPerson(personident),tokenXToken)
        val person = res.data?.hentPdlPerson
        return person?.toSøker()
    }

    suspend fun hentBarn(personident: String, tokenXToken:String):List<Barn>{
        val barnRelasjon:List<String>? = hentBarnRelasjon(personident,tokenXToken)?.mapNotNull { it.relatertPersonsIdent }?.toList()
        val barn:List<PdlPerson> = hentBarn(barnRelasjon?: emptyList(), tokenXToken)
            .filter { it.myndig() } //TODO: filtrer død og adressebeskyttelse også kinda
            .filter { it.beskyttet().not() }
            .filter { it.død().not() }
        return barn.map { Barn(it.fulltNavn(),LocalDate.parse(it.foedsel?.firstOrNull()?.foedselsdato),it.fnr) }
    }

    private suspend fun hentBarnRelasjon(personident: String, tokenXToken:String) = query(PdlRequest.hentBarnRelasjon(personident),tokenXToken).data?.hentPdlPerson?.foreldreBarnRelasjon

    private suspend fun hentBarn(list: List<String>, tokenXToken:String):List<PdlPerson>{
        return list.map { fnr ->
            val barnInfo = query(hentBarnInfo(fnr),tokenXToken).data?.hentPdlPerson
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

    private suspend fun query(query: PdlRequest, tokenXToken:String): PdlResponse {
        val token = tokenProvider.getOnBehalfOfToken(tokenXToken)
        val request = httpClient.post(pdlConfig.baseUrl) {
            accept(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("TEMA", "AAP")
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(query)
        }
        return request.body()
    }

    private val callId: String get() = UUID.randomUUID().toString().also { SECURE_LOGGER.info("calling pdl with call-id $it") }

}