package com.github.kuzznya.timepicker.api

import com.github.kuzznya.timepicker.exception.ResponseStatusException
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

object ExceptionHandler : Handler<RoutingContext> {

    private val log = LoggerFactory.getLogger(ExceptionHandler::class.java)

    override fun handle(ctx: RoutingContext) {
        if (ctx.response().headWritten()) {
            runCatching { ctx.response().close() }
            return
        }
        val failure = ctx.failure()
        if (failure is ResponseStatusException) {
            ctx.response()
                .setStatusCode(failure.status.code())
                .end(
                    JsonObject()
                    .put("message", failure.message ?: failure.status.reasonPhrase())
                    .encode())
            log.info("Resolved: {} {}", failure.status.code(), failure.message)
        } else {
            ctx.response()
                .setStatusCode(500)
                .end(JsonObject().put("message", "Internal Server Error").encode())
            log.error("Unhandled exception", failure)
        }
    }
}
