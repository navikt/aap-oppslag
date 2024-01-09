package oppslag.integrasjoner.behandler

data class RegistrertBehandler(
    val navn: String,
    val type: Type, 
    val behandlerRef: String,
    val kontaktinformasjon: KontaktInformasjon
) {
    data class KontaktInformasjon(
        val kontor: String?,
        val adresse: Adresse?,
        var telefon: String?
    )

    enum class Type { FASTLEGE, SYKMELDER }
}

