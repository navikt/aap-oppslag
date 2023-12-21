package oppslag.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate

data class Søker(val navn: String,
                 val fnr: String,
                 @JsonIgnore
                 val erBeskyttet: Boolean,
                 val adresse: String? = null,
                 val fødseldato: LocalDate? = null
)

data class Barn(
    val navn: String?,
    val fødselsdato: LocalDate?,
    val fnr: String?,
)