package com.github.kuzznya.timepicker.api

import com.github.kuzznya.timepicker.config.getOAuth2Options
import com.github.kuzznya.timepicker.exception.ResponseStatusException
import com.github.kuzznya.timepicker.model.User
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

object AuthRouterConfig {

    private val log = LoggerFactory.getLogger(AuthRouterConfig::class.java)

    fun create(vertx: Vertx, oauth2: OAuth2Auth, config: JsonObject): Router {
        val router = Router.router(vertx)
        router.route().failureHandler(ExceptionHandler)
        router.post().handler { ctx -> authenticate(oauth2, config, ctx) }
        return router
    }

    private fun authenticate(oauth2: OAuth2Auth, config: JsonObject, ctx: RoutingContext) {
        val code = ctx.bodyAsJson.getString("code") ?: throw ResponseStatusException(
            HttpResponseStatus.BAD_REQUEST, "Body parameter 'code' should be provided")
        val redirectUri = ctx.bodyAsJson.getString("redirectUri") ?:
        config.getJsonObject("oauth2", JsonObject()).getString("redirect-uri") ?:
        throw Exception("oauth2.redirect-uri should be defined")

        val options = getOAuth2Options(config)

        oauth2.authenticate(
            JsonObject()
                .put("code", code)
                .put("redirectUri", redirectUri)
                .put("client_id", options.clientId)
                .put("client_secret", options.clientSecret))
            .onSuccess { user ->
                runCatching {
                val userInfo = user.attributes().getJsonObject("idToken")
                val appUser = User(
                    username = userInfo.getString("preferred_username"),
                    name = userInfo.getString("given_name"),
                    lastName = userInfo.getString("family_name"),
                    email = userInfo.getString("email"))
                log.debug("User {} authenticated", appUser.username)
                ctx.response().end(JsonObject.mapFrom(appUser).encode()) }
            }.onFailure { err ->
                log.error("Keycloak auth error", err)
                ctx.fail(ResponseStatusException(HttpResponseStatus.FORBIDDEN, "Exchange failed, cannot retrieve Keycloak access token"))
            }
    }
}
