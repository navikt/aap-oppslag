internal data class PdlRequest(val query: String, val variables: Variables) {
    data class Variables(val ident: String)

    companion object {
        fun hentPerson(personident: String) = PdlRequest(
            query = person.replace("\n", ""),
            variables = Variables(personident),
        )

        fun hentBarnRelasjon(personident: String) = PdlRequest(
            query = barnRelasjon.replace("\n", ""),
            variables = Variables(personident),
        )

        fun hentBarnInfo(personident: String) = PdlRequest(
            query = barn.replace("\n", ""),
            variables = Variables(personident),
        )
    }
}


private const val ident = "\$ident"

private val barnRelasjon = """
    query($ident: ID!) {
        hentPerson(ident: $ident) {
            forelderBarnRelasjon {
                relatertPersonsIdent
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

private val barn = """
    query($ident: ID!) {
        hentPerson(ident: $ident) {
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
    }
""".trimIndent()