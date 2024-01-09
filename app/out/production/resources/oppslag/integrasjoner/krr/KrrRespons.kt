package oppslag.integrasjoner.krr

data class KrrRespons(
    val kanVarsles: Boolean? = false,
    val aktiv: Boolean? = false,
    val epostadresse: String? = null,
    val mobiltelefonnummer: String? = null
) {
    fun tilKontaktinfo() =
        if (aktiv == true && kanVarsles == true) {
            Kontaktinformasjon(epostadresse, mobiltelefonnummer)
        } else {
            Kontaktinformasjon()
        }
}
