package oppslag.integrasjoner.behandler

data class RegistrertBehandler(
    val navn: String,
    val behandlerRef: String,
    val kontaktinformasjon: KontaktInformasjon
) {
    data class KontaktInformasjon(
        val kontor: String?,
        val adresse: String?,
        var telefon: String?
    )

    enum class Type { FASTLEGE, SYKMELDER }
}

