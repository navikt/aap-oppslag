package oppslag.integrasjoner.behandler

import kotlinx.serialization.Serializable

@Serializable
data class RegistrertBehandler(
    val navn: String,
    val behandlerRef: String,
    val kontaktinformasjon: KontaktInformasjon
) {
    @Serializable
    data class KontaktInformasjon(
        val kontor: String?,
        val adresse: String?,
        var telefon: String?
    )

    @Serializable
    enum class Type { FASTLEGE, SYKMELDER }
}

