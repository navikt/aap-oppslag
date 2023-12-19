

internal data class PdlRequest(val query: String, val variables: Variables) {
    data class Variables(val ident: String)

    companion object {
        fun hentPerson(personident: String) = PdlRequest(
            query = person.replace("\n", ""),
            variables = Variables(personident),
        )
    }
}


private const val ident = "\$ident"

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
