package oppslag

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText
import oppslag.integrasjoner.pdl.PdlException
import oppslag.integrasjoner.saf.DokumentIkkeFunnet
import oppslag.integrasjoner.saf.SafException
import org.slf4j.LoggerFactory

object StatusPagesConfigHelper {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun setup(): StatusPagesConfig.() -> Unit = {
        exception<Throwable> { call, cause ->
            when (cause) {
                is PdlException -> {
                    logger.error("Uhåndtert feil ved kall til '{}'", call.request.local.uri, cause)
                    call.respondText(text = "Feil i PDL: ${cause.message}", status = HttpStatusCode.InternalServerError)
                }
                is SafException -> {
                    logger.error("Uhåndtert feil ved kall til '{}'", call.request.local.uri, cause)
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.InternalServerError)
                }
                is DokumentIkkeFunnet -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.NotFound)
                }
                else -> {
                    logger.error("Uhåndtert feil ved kall til '{}'", call.request.local.uri, cause)
                    call.respondText(
                        text = "Uhåndtert feil: ${cause.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
    }
}