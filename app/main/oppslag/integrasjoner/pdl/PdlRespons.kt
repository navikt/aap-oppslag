package oppslag.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate


data class Søker(val navn: String,
                 val fnr: String,
                 @JsonIgnore
                 val erBeskyttet: Boolean,
                 val adresse: String? = null,
                 val fødseldato: LocalDate? = null
)

data class Barn(
    val navn: String?,
    val fødselsdato: LocalDate?,
    val fnr: String?,
)

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
    val bostedsadresse: List<PdlBostedsadresse>?=null,
    val foedsel: List<PdlFoedsel>?,
    val foreldreBarnRelasjon: List<PdlForelderBarnRelasjon>?=null,
    val fnr: String?=null,
    val doedsfall: Set<PDLDødsfall>?=null
) {
    fun toSøker(): Søker {
        val adresse = bostedsadresse?.firstOrNull()?.vegadresse?.adresse()
        val fødselsdato = foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it) }
        return Søker(
            navn = this.fulltNavn() ?: "",
            fnr = fnr ?: "",
            erBeskyttet = adressebeskyttelse?.any {
                it.gradering in listOf("FORTROLIG", "STRENGT_FORTROLIG_UTLAND", "STRENGT_FORTROLIG")
            } == true,
            adresse = adresse,
            fødseldato = fødselsdato
        )
    }

    fun toBarn(): Barn {
        val adresse = bostedsadresse?.firstOrNull()?.vegadresse?.adresse()
        val fødselsdato = foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it) }
        return Barn(
            navn = this.fulltNavn() ?: "",
            fnr = fnr ?: "",
            fødselsdato = fødselsdato
        )
    }

    fun fulltNavn(): String? = navn?.firstOrNull()?.let { "${it.fornavn} ${it.mellomnavn ?: ""} ${it.etternavn}" }
    fun myndig():Boolean = this.foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it).plusYears(18) }?.isBefore(LocalDate.now()) ?: false
    fun beskyttet() = this.adressebeskyttelse?.any {
        it.gradering in listOf("FORTROLIG", "STRENGT_FORTROLIG_UTLAND", "STRENGT_FORTROLIG")
    } == true

    data class PDLDødsfall(@JsonProperty("doedsdato") val dødsdato: LocalDate)
    fun død() = this.doedsfall?.any() ?: false
}

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
){
    fun adresse(): String = "$adressenavn ${husnummer ?: ""}${husbokstav ?: ""}, $postnummer"
}

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
