package oppslag.integrasjoner.pdl

import java.time.LocalDate

internal fun PdlPerson.toSøker(): Søker {
    val adresse = bostedsadresse?.firstOrNull()?.vegadresse?.adresse()
    val fødselsdato = foedselsdato?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it) }
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
    val fødselsdato = foedselsdato?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it) }
    return Barn(
        navn = this.fulltNavn() ?: "",
        fødselsdato = fødselsdato,
        pdlStatus = this.code?.toPdlStatus() ?: PdlStatus.OK
    )
}

internal fun PdlPerson.toNavn(): Navn {
    val navn = this.navn?.firstOrNull() ?: error("Navn mangler i PDL respons")
    return Navn(navn.fornavn, navn.etternavn)
}

internal fun Code.toPdlStatus(): PdlStatus = PdlStatus.valueOf(this.name.uppercase())

internal fun PdlPerson.fulltNavn(): String? =
    navn?.firstOrNull()?.let { "${it.fornavn} ${it.mellomnavn ?: ""} ${it.etternavn}" }

internal fun PdlPerson.myndig(): Boolean =
    this.foedselsdato?.firstOrNull()?.foedselsdato?.let { LocalDate.parse(it).plusYears(18) }?.isBefore(
        LocalDate.now()
    ) ?: false

internal fun PdlPerson.strengtFortroligAdresse() = this.adressebeskyttelse?.any {
    it.gradering in listOf("STRENGT_FORTROLIG_UTLAND", "STRENGT_FORTROLIG")
} ?: false

internal fun PdlPerson.død() = this.doedsfall?.any() ?: false

internal fun PdlVegadresse.adresse(): String = "$adressenavn ${husnummer ?: ""}${husbokstav ?: ""}, $postnummer"
