package oppslag.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

internal data class PdlResponse(
    val data: PdlData?,
    val errors: List<PdlError>?,
)

internal data class PdlData(
    val hentPdlPerson: PdlPerson?,
)

internal data class PdlPerson(
    val adressebeskyttelse: List<Adressebeskyttelse>?,
    val navn: List<PdlNavn>?,
    val bostedsadresse: List<PdlBostedsadresse>? = null,
    val foedsel: List<PdlFoedsel>?,
    val foreldreBarnRelasjon: List<PdlForelderBarnRelasjon>? = null,
    val fnr: String? = null,
    val doedsfall: Set<PDLDødsfall>? = null
)

internal data class PDLDødsfall(
    @JsonProperty("doedsdato") val dødsdato: LocalDate
)

internal data class PdlForelderBarnRelasjon(
    val relatertPersonsIdent: String?
)

internal data class PdlFoedsel(
    val foedselsdato: String?
)

internal data class PdlBostedsadresse(
    val vegadresse: PdlVegadresse?
)

internal data class PdlVegadresse(
    val adressenavn: String,
    val husbokstav: String?,
    val husnummer: String?,
    val postnummer: String,
)

internal data class Adressebeskyttelse(
    val gradering: String
)

internal data class PdlNavn(
    val fornavn: String,
    val etternavn: String,
    val mellomnavn: String?
)

internal data class PdlError(
    val message: String,
    val locations: List<PdlErrorLocation>,
    val path: List<String>?,
    val extensions: PdlErrorExtension
)

internal data class PdlErrorExtension(
    val code: String?,
    val classification: String
)

internal data class PdlErrorLocation(
    val line: Int?,
    val column: Int?
)
