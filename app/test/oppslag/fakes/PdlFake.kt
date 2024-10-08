package oppslag.fakes

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate


fun Application.PdlFake() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        post {
            val code = oppslag.integrasjoner.pdl.Code.ok
            val body = call.receive<String>()
            if("hentPersonBolk" in body){
                call.respondText(
                    """
                    {
                        "data": {
                            "hentPersonBolk": [
                                {
                                    "ident": "12345678910",
                                    "person": {
                                        "navn": [
                                            {
                                                "fornavn": "død",
                                                "mellomnavn": "Mellomnavn",
                                                "etternavn": "Nordmann"
                                            }
                                        ],
                                        "foedselsdato": [
                                            {
                                                "foedselsdato": "2020-01-01"
                                            }
                                        ],
                                        "doedsfall": [
                                            {
                                                "doedsdato": "2021-01-01"
                                            }
                                        ],
                                        "adressebeskyttelse": [
                                            {
                                                "gradering": "UGRADERT"
                                            }
                                        ],
                                        "code":"${code}"
                                    },
                                    "code":"${code}"
                                },
                                {
                                    "ident": "12345678911",
                                    "person": {
                                        "navn": [
                                            {
                                                "fornavn": "kari",
                                                "mellomnavn": "Mellomnavn",
                                                "etternavn": "Nordmann"
                                            }
                                        ],
                                        "foedselsdato": [
                                            {
                                                "foedselsdato": "${LocalDate.now().minusYears(17)}"
                                            }
                                        ],
                                        "doedsfall": [],
                                        "adressebeskyttelse": [
                                            {
                                                "gradering": "UGRADERT"
                                            }
                                        ],
                                        "code":"${code}"
                                    },
                                    "code":"${code}"
                                },
                                {
                                    "ident": "12345678912",
                                    "person": {
                                        "navn": [
                                            {
                                                "fornavn": "Myndig",
                                                "mellomnavn": "Mellomnavn",
                                                "etternavn": "Nordmann"
                                            }
                                        ],
                                        "foedselsdato": [
                                            {
                                                "foedselsdato": "${LocalDate.now().minusYears(20)}"
                                            }
                                        ],
                                        "doedsfall": [],
                                        "adressebeskyttelse": [
                                            {
                                                "gradering": "STRENGT_FORTROLIG"
                                            }
                                        ],
                                        "code":"${code}"
                                    },
                                    "code":"${code}"
                                }
                            ]
                        }
                    }
                    """.trimIndent(),
                    contentType = ContentType.Application.Json
                )
            } else if("forelderBarnRelasjon" in body){
              call.respondText(
                    """
                    {
                        "data": {
                            "hentPerson": {
                                "forelderBarnRelasjon": [
                                    {
                                        "relatertPersonsIdent": "12345678910"
                                    },
                                    {
                                        "relatertPersonsIdent": "12345678911"
                                    },
                                    {
                                        "relatertPersonsIdent": "12345678912"
                                    }
                                ]
                            }
                        }
                    }
                    """.trimIndent(),
                contentType = ContentType.Application.Json
              )
            } else {
                call.respondText(
                    """
                    {
                        "data": {
                            "hentPerson": {
                                "navn": [
                                    {
                                        "fornavn": "Ola",
                                        "mellomnavn": "Mellomnavn",
                                        "etternavn": "Nordmann"
                                    }
                                ],
                                "foedselsdato": [
                                    {
                                        "foedselsdato": "1990-01-01"
                                    }
                                ],
                                "bostedsadresse": [
                                    {
                                        "vegadresse": {
                                            "adressenavn": "Osloveien",
                                            "husbokstav": "A",
                                            "husnummer": "1",
                                            "postnummer": "1234"
                                        }
                                    }
                                ],
                                "adressebeskyttelse": [
                                    {
                                        "gradering": "UGRADERT"
                                    }
                                ]
                            }
                        }
                    }
                    """.trimIndent(),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}