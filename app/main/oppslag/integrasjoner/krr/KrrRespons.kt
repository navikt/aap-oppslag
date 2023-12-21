package oppslag.integrasjoner.krr

import com.fasterxml.jackson.annotation.JsonAlias

data class KrrRespons(
    val kanVarsles: Boolean? = false,
    val aktiv: Boolean? = false,
    @JsonAlias("epostadresse") val epost: String? = null,
    @JsonAlias("mobiltelefonnummer") val mobil: String? = null
) {
    fun tilKontaktinfo() = //TODO: sjekk reservert
        if (aktiv==true && kanVarsles==true) {
            Kontaktinformasjon(epost, mobil)
        } else {
            Kontaktinformasjon()
        }
}


data class Kontaktinformasjon(
    val epost: String? = null,
    val mobil: String? = null
)