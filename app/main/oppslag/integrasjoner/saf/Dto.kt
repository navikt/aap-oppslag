package oppslag.integrasjoner.saf

import java.time.LocalDateTime

data class Dokument(
    val journalpostId: String,
    val dokumentId: String,
    val tittel: String?,
    val type: String,
    val innsendingId: String?,
    val dato: LocalDateTime
)
