package oppslag.integrasjoner.behandler

data class BehandlerRespons(
    val type: String, //  FASTLEGE, SYKMELDER
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
        navn = "${this.fornavn} ${this.mellomnavn ?: ""} ${this.etternavn}",
        type = RegistrertBehandler.Type.valueOf(this.type),
        behandlerRef = this.behandlerRef,
        kontaktinformasjon = RegistrertBehandler.KontaktInformasjon(
            kontor,
            adresse,
            telefon
        )
    )
}

data class Adresse(
    val adressenavn: String?,
    val husbokstav: String?,
    val husnummer: String?,
    val postnummer: String?,
    val poststed: String?
)
