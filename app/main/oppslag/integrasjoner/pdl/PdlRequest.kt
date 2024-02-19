package oppslag.integrasjoner.pdl

import oppslag.graphql.asQuery

internal data class PdlRequest(val query: String, val variables: Variables) {
    data class Variables(val ident: String? = null, val identer: List<String>? = null)

    companion object {
        fun hentPerson(personident: String) = PdlRequest(
            query = person.asQuery(),
            variables = Variables(ident = personident),
        )

        fun hentNavn(personident: String) = PdlRequest(
            query = navn.asQuery(),
            variables = Variables(ident = personident),
        )

        fun hentBarnRelasjon(personident: String) = PdlRequest(
            query = barnRelasjon.asQuery(),
            variables = Variables(ident = personident),
        )

        fun hentBarnInfo(personidenter: List<String>) = PdlRequest(
            query = barnBolk.asQuery(),
            variables = Variables(identer = personidenter),
        )
    }
}


private const val ident = "\$ident"
private const val identer = "\$identer"

private val barnRelasjon = """
    query($ident: ID!) {
        hentPerson(ident: $ident) {
            forelderBarnRelasjon {
                relatertPersonsIdent
            }
        }
    }
""".trimIndent()

private val navn  = """
    query($ident: ID!) {
        hentPerson(ident: $ident) {
            navn {
                fornavn,
                etternavn,
            }
        }
    }
""".trimIndent()

private val person = """
    query($ident: ID!) {
        hentPerson(ident: $ident) {
            foedsel {
                foedselsdato
            },
            adressebeskyttelse {
                gradering
            },
            bostedsadresse {
                vegadresse {
                    adressenavn
                    husbokstav
                    husnummer
                    postnummer
                }
            }
            navn {
                fornavn,
                etternavn,
                mellomnavn
            }
        }
    }
""".trimIndent()

private val barnBolk = """
    query($identer: [ID!]!) {
        hentPersonBolk(identer: $identer) {
            ident,
            person {
                adressebeskyttelse {
                    gradering
                },
                doedsfall {
                    doedsdato
                },
                foedsel {
                    foedselsdato
                },
                navn {
                    fornavn,
                    etternavn,
                    mellomnavn
               }
            }
            code
        }
    }
""".trimIndent()
