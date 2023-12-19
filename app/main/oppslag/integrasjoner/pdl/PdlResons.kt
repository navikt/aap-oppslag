package oppslag.integrasjoner.pdl

internal data class PdlResponse(
    val data: PdlData?,
    val errors: List<PdlError>?,
)

internal data class PdlData(
    val hentPerson: Person?,
)

internal data class Person(
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val navn: List<PdlNavn>,
    val bostedsadresse: List<PdlBostedsadresse>,
    val foedsel: List<PdlFoedsel>,
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
