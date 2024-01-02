package oppslag.integrasjoner.pdl

import java.time.LocalDate

internal fun PdlPerson.toSøker(): Søker {
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

internal fun PdlPerson.toBarn(): Barn {
    val fødselsdato = foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it) }
    return Barn(
        navn = this.fulltNavn() ?: "",
        fødselsdato = fødselsdato
    )
}

internal fun PdlPerson.fulltNavn(): String? =
    navn?.firstOrNull()?.let { "${it.fornavn} ${it.mellomnavn ?: ""} ${it.etternavn}" }

internal fun PdlPerson.myndig(): Boolean =
    this.foedsel?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it).plusYears(18) }?.isBefore(
        LocalDate.now()
    ) ?: false

internal fun PdlPerson.beskyttet() = this.adressebeskyttelse?.any {
    it.gradering in listOf("FORTROLIG", "STRENGT_FORTROLIG_UTLAND", "STRENGT_FORTROLIG")
} ?: false

internal fun PdlPerson.død() = this.doedsfall?.any() ?: false

internal fun PdlVegadresse.adresse(): String = "$adressenavn ${husnummer ?: ""}${husbokstav ?: ""}, $postnummer"
