package oppslag.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

internal data class PdlResponse(
    val data: PdlData?,
    val errors: List<PdlError>?,
)

internal data class PdlData(
    val hentPerson: Person?,
)

internal data class Person(
    val adressebeskyttelse: List<Adressebeskyttelse>?,
    val navn: List<PdlNavn>?,
    val bostedsadresse: List<PdlBostedsadresse>?,
    val foedsel: List<PdlFoedsel>?,
    val foreldreBarnRelasjon: List<PdlForelderBarnRelasjon>?,
    val fnr: String?
) {
    fun fulltNavn(): String? = navn?.firstOrNull()?.let { "${it.fornavn} ${it.mellomnavn ?: ""} ${it.etternavn}" }
    fun myndig():Boolean = this.foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it).plusYears(18) }?.isBefore(LocalDate.now()) ?: false

}

internal data class Barn(
    val navn: String?,
    val fødselsdato: LocalDate?,
    val fnr: String?,
)

internal data class PdlForelderBarnRelasjon(
    val relatertPersonsIdent: String?
)

internal data class PdlFoedsel(val foedselsdato: String?)

internal data class PdlBostedsadresse(val vegadresse: PdlVegadresse?)
internal data class PdlVegadresse(
    val adressenavn: String,
    val husbokstav: String?,
    val husnummer: String?,
    val postnummer: String,
)

internal data class Adressebeskyttelse(val gradering: String)
internal data class PdlNavn(val fornavn: String, val etternavn: String, val mellomnavn: String?)


internal data class PdlError(
    val message: String,
    val locations: List<PdlErrorLocation>,
    val path: List<String>?,
    val extensions: PdlErrorExtension
)

internal data class PdlErrorExtension(val code: String?, val classification: String)
internal data class PdlErrorLocation(val line: Int?, val column: Int?)
