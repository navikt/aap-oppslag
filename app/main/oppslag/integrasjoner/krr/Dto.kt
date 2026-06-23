package oppslag.integrasjoner.krr

import kotlinx.serialization.Serializable

@Serializable
data class Kontaktinformasjon(
    val epost: String? = null,
    val mobil: String? = null
)
