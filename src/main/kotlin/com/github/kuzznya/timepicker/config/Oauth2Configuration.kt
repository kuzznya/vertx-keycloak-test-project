package com.github.kuzznya.timepicker.config

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.ext.auth.oauth2.OAuth2Options
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth

fun getOAuth2Options(config: JsonObject): OAuth2Options {
    val authConfig = config.getJsonObject("oauth2") ?: throw Exception("oauth2.* properties not set")
    return OAuth2Options()
        .setFlow(OAuth2FlowType.AUTH_CODE)
        .setSite(authConfig.getString("keycloak-url", "http://localhost:8000/auth/realms/timepicker"))
        .setClientId(authConfig.getString("client-id", "timepicker-app"))
        .setClientSecret(config.getString("KEYCLOAK_SECRET"))
        .apply { extraParameters = JsonObject()
            .put("client_id", this.clientId)
            .put("client_secret", this.clientSecret)
        }
}

fun configureOauth2(vertx: Vertx, config: JsonObject): Future<OAuth2Auth> =
    KeycloakAuth.discover(vertx, getOAuth2Options(config))
