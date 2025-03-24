package oppslag.integrasjoner.krr

typealias PersonIdent = String

data class KrrRespons(
    val personer: Map<PersonIdent, KrrPerson>? = null,
    val feil: Map<PersonIdent, String>? = null,
)

data class KrrPerson(
    val kanVarsles: Boolean? = false,
    val aktiv: Boolean? = false,
    val epostadresse: String? = null,
    val mobiltelefonnummer: String? = null,
) {
    fun tilKontaktinfo() =
        if (aktiv == true && kanVarsles == true) {
            Kontaktinformasjon(epostadresse, mobiltelefonnummer)
        } else {
            Kontaktinformasjon()
        }
}
