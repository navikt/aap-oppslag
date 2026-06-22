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

fun Application.SafFake() {

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        get("/rest/hentdokument/400000000/23423535/{arkivtype}"){
            when (call.parameters["arkivtype"]) {
                "ORIGINAL" -> call.respondText("{}", ContentType.Application.Json)
                else -> call.respondBytes(MINIMAL_PDF, ContentType.Application.Pdf)
            }
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
            } else if ("dokumentoversiktSelvbetjening" in body) {
                call.respondText("""
                    {
                      "data": {
                        "dokumentoversiktSelvbetjening": {
                          "journalposter": [
                            {
                              "journalpostId": "400000000",
                              "journalposttype": "I",
                              "eksternReferanseId": "639af3ed-b557-4829-a057-14f8e9d48052",
                              "tittel": "Søknad om arbeidsavklaringspenger",
                              "relevanteDatoer": [
                                {
                                  "dato": "2020-02-02T12:00:00.000000",
                                  "datotype": "DATO_OPPRETTET"
                                }
                              ],
                              "dokumenter": [
                                {
                                  "dokumentInfoId": "23423535",
                                  "brevkode": null,
                                  "tittel": "Søknad",
                                  "dokumentvarianter": [
                                    {
                                      "variantformat": "ARKIV",
                                      "brukerHarTilgang": true,
                                      "filtype": "PDF"
                                    }
                                  ]
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
                call.respondText(
                    """{"data":null,"errors":null,"extensions":null}""",
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}

// Minimal valid PDF with correct xref offsets — displays "test pdf" on a blank page
private val MINIMAL_PDF: ByteArray = (
    "%PDF-1.4\n" +
    "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
    "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n" +
    "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 200 200]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n" +
    "4 0 obj<</Length 40>>\n" +
    "stream\n" +
    "BT /F1 12 Tf 10 180 Td (test pdf) Tj ET\n" +
    "endstream\n" +
    "endobj\n" +
    "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n" +
    "xref\n" +
    "0 6\n" +
    "0000000000 65535 f \n" +
    "0000000009 00000 n \n" +
    "0000000052 00000 n \n" +
    "0000000101 00000 n \n" +
    "0000000211 00000 n \n" +
    "0000000297 00000 n \n" +
    "trailer<</Size 6/Root 1 0 R>>\n" +
    "startxref\n" +
    "358\n" +
    "%%EOF"
).toByteArray(Charsets.ISO_8859_1)
