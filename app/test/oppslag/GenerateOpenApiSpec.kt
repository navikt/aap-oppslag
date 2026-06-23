package oppslag

import io.ktor.openapi.OpenApiDoc
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.plugin
import io.ktor.server.routing.openapi.plus
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.getAllRoutes
import io.ktor.server.testing.testApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File

class GenerateOpenApiSpec {

    @Test
    fun `generer og lagre openapi-spec`() {
        Fakes().use { fakes ->
            testApplication {
                application { api(TestConfig.default(fakes)) }
                startApplication()

                val allRoutes = application.plugin(RoutingRoot.Plugin).getAllRoutes()

                val spec = OpenApiDoc.build {
                    info = OpenApiInfo(title = "aap-oppslag", version = "1.0")
                } + allRoutes

                File("../openapi.json").writeText(
                    Json { prettyPrint = true }.encodeToString(OpenApiDoc.serializer(), spec)
                )
            }
        }
    }
}
