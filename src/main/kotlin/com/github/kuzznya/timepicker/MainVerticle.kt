package com.github.kuzznya.timepicker

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kuzznya.timepicker.api.AuthRouterConfig
import com.github.kuzznya.timepicker.api.ExceptionHandler
import com.github.kuzznya.timepicker.config.configureOauth2
import com.github.kuzznya.timepicker.config.createConfigRetriever
import com.github.kuzznya.timepicker.config.runMigrations
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.healthchecks.HealthCheckHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start(startPromise: Promise<Void>) {
        val configRetriever = createConfigRetriever(vertx)
        configRetriever.getConfig { config ->
            prepare(vertx, config.result()).onSuccess {
                log.info("Preparation finished, starting HTTP server")
                start(startPromise, config.result())
            }.onFailure { e -> startPromise.fail(e) }
        }
    }

    private fun prepare(vertx: Vertx, config: JsonObject): Future<Unit> =
        runMigrations(vertx, config).map { configureJackson() }

    private fun start(startPromise: Promise<Void>, config: JsonObject) {
        val port = config.getInteger("port", 8080)
        vertx
            .createHttpServer()
            .requestHandler(router(vertx, config))
            .listen(port) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    log.info("HTTP server started on port $port")
                } else {
                    startPromise.fail(http.cause())
                }
            }
    }

    private fun router(vertx: Vertx, config: JsonObject) : Router {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.route().produces("application/json").handler(ResponseContentTypeHandler.create())
        router.route().failureHandler(ExceptionHandler)
        router.route().handler(TimeoutHandler.create(5000))
        router.route().handler(CorsHandler.create("*")
            .allowedHeaders(setOf("*"))
            .allowedMethods(setOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS)))
        router.get("/health*").handler(HealthCheckHandler.create(vertx))
        configureOauth2(vertx, config).onSuccess { oauth2 ->
            router.mountSubRouter("/auth", AuthRouterConfig.create(vertx, oauth2, config))
            router.get("/secured").handler { ctx -> ctx.end(JsonObject().put("a", "a").encode()) }
        }.onFailure { ex ->
            log.error("Keycloak initialization error", ex)
            vertx.close()
        }
        return router
    }

    private fun configureJackson() {
        DatabindCodec.mapper().apply {
            registerKotlinModule()
        }

        DatabindCodec.prettyMapper().apply {
            registerKotlinModule()
        }
    }
}
