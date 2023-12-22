package oppslag.integrasjoner.behandler

data class RegistrertBehandler(
    val navn: Navn,
    val kontaktinformasjon: KontaktInformasjon
) {
    data class KontaktInformasjon(
        val kontor: String?,
        val adresse: Adresse?,
        var telefon: String?
    )
}

data class Navn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String
)
