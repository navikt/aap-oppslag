@file:UseSerializers(LocalDateTimeSerializer::class)

package oppslag.integrasjoner.saf

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import oppslag.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Dokument(
    val journalpostId: String,
    val dokumentId: String,
    val tittel: String?,
    val type: String,
    val innsendingId: String?,
    val dato: LocalDateTime
)
