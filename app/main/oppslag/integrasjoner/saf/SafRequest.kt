package oppslag.integrasjoner.saf

import oppslag.graphql.asQuery

internal data class SafRequest(val query: String, val variables: Variables) {
    data class Variables(val ident: String)

    companion object {
        fun hentDokumenter(personIdent: String) = SafRequest(
            query = dokumenter.asQuery(),
            variables = Variables(personIdent)
        )
    }
}

private const val ident = "\$ident"

private val dokumenter = """
    query($ident: String!) {
        dokumentoversiktSelvbetjening(ident: $ident, tema: [AAP]) {
            journalposter {
                journalpostId
                journalposttype
                eksternReferanseId
                relevanteDatoer {
                    dato
                    datotype
                }
                dokumenter {
                    dokumentInfoId
                    brevkode
                    tittel
                    dokumentvarianter {
                        variantformat
                        brukerHarTilgang
                        filtype
                    }
                }
            }
        }
    }
""".trimIndent()

