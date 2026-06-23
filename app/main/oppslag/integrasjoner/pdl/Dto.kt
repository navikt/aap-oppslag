@file:UseSerializers(LocalDateSerializer::class)

package oppslag.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import oppslag.serializers.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Søker(
    val navn: String,
    val fnr: String,
    @JsonIgnore
    @Transient
    val erBeskyttet: Boolean = false,
    val adresse: String? = null,
    val fødseldato: LocalDate? = null,
    val erUnderAttenÅr: Boolean
)

@Serializable
data class Navn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
)

@Serializable
data class Barn(
    val navn: String?,
    val fødselsdato: LocalDate?,
    val pdlStatus: PdlStatus
)

@Serializable
enum class PdlStatus {
    OK, NOT_FOUND, BAD_REQUEST
}