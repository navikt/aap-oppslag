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
import oppslag.integrasjoner.krr.KrrRespons
import oppslag.integrasjoner.saf.Dokument

fun Application.SafFake() {

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        get("/rest/hentdokument/400000000/23423535/ORIGINAL"){
            call.respond("{}".toByteArray())
        }

        post("/graphql"){
            val body = call.receive<String>()

            if("journalpostById" in body) {
                call.respondText("""
                    {
                      "data": {
                        "journalpostById": {
                          "journalpostId": "400000000",
                          "tittel": "Søknad om arbeidsavklaringspenger",
                          "journalposttype": "I",
                          "eksternReferanseId": "639af3ed-b557-4829-a057-14f8e9d48052",
                          "relevanteDatoer": [
                            {
                                "dato": "2020-02-02T12:00:00.000000",
                                "datotype": "DATO_OPPRETTET"
                            }
                          ],
                          "dokumenter": [
                            {
                                "dokumentInfoId": "23423535",
                                "tittel": "Søknad",
                                "dokumentvarianter": [
                                    {
                                        "variantformat": "ARKIV",
                                        "brukerHarTilgang": true,
                                        "filtype": "PDF"
                                    },
                                    {
                                        "variantformat": "ORIGINAL",
                                        "brukerHarTilgang": true,
                                        "filtype": "JSON"
                                    }
                                ]
                            }
                          ]
                        }
                      }
                    }
                """.trimIndent(),
                    contentType = ContentType.Application.Json)
            } else {
                print("FEIL KALL")
                call.respond(res(null))
            }
        }
    }
}

private data class res(val data:String?)