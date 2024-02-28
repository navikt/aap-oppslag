package oppslag.integrasjoner.saf

import oppslag.graphql.asQuery

internal data class SafRequest(val query: String, val variables: Variables) {
    data class Variables(val ident: String? = null, val journalpostId: String? = null)

    companion object {
        fun hentDokumenter(personIdent: String) = SafRequest(
            query = dokumenter.asQuery(),
            variables = Variables(ident = personIdent)
        )

        fun hentJournalpost(journalpostId: String) = SafRequest(
            query = journalpost.asQuery(),
            variables = Variables(journalpostId = journalpostId)
        )
    }
}

private const val ident = "\$ident"
private const val journalpostId = "\$journalpostId"

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

private val journalpost = """
    query journalpostById($journalpostId: String!) {
        journalpostById(journalpostId: $journalpostId) {
            journalpostId
            tittel
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
""".trimIndent()

