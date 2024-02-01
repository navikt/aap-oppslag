package oppslag.integrasjoner.saf

import oppslag.graphql.GraphQLError
import oppslag.graphql.GraphQLExtensions
import java.time.LocalDateTime

internal data class SafRespons(
    val data: SafData?,
    val errors: List<GraphQLError>?,
    val extensions: GraphQLExtensions?
)

internal data class SafData(
    val dokumentoversiktSelvbetjening: SafDokumentoversikt?
)

internal data class SafDokumentoversikt(
    val journalposter: List<SafJournalpost?>
)

internal data class SafJournalpost(
    val journalpostId: String,
    val journalposttype: String,
    val eksternReferanseId: String?,
    val relevanteDatoer: List<SafRelevantDato>,
    val dokumenter: List<SafDokumentInfo?>?
)

internal data class SafRelevantDato(
    val dato: LocalDateTime,
    val datotype: SafDatoType
)

internal enum class SafDatoType {
    DATO_OPPRETTET, DATO_SENDT_PRINT, DATO_EKSPEDERT,
    DATO_JOURNALFOERT, DATO_REGISTRERT,
    DATO_AVS_RETUR, DATO_DOKUMENT
}

internal data class SafDokumentInfo(
    val dokumentInfoId: String,
    val brevkode: String?,
    val tittel: String?,
    val dokumentvarianter: List<SafDokumentvariant?>
)

internal data class SafDokumentvariant(
    val variantformat: SafVariantformat,
    val brukerHarTilgang: Boolean,
    val filtype: String
)

internal enum class SafVariantformat {
    ARKIV, SLADDET, ORIGINAL
}

