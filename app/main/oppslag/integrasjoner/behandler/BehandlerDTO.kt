package oppslag.integrasjoner.behandler

data class BehandlerDTO(
    val type: String,
    val kategori: String,
    val behandlerRef: String,
    val fnr: String?,
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val orgnummer: String?,
    val kontor: String?,
    val adresse: Adresse?,
    val postnummer: String?,
    val poststed: String?,
    val telefon: String?
) {

    fun tilBehandler() = RegistrertBehandler(
        Navn(fornavn, mellomnavn, etternavn),
        RegistrertBehandler.KontaktInformasjon(
            kontor,
            adresse,
            telefon
        )
    )

}

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

data class Adresse(
    val adressenavn: String?,
    val husbokstav: String?,
    val husnummer: String?,
    val postnummer: String?,
    val poststed: String?
)