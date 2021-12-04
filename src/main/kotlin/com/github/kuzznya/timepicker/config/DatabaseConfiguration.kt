package com.github.kuzznya.timepicker.config

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.SqlClient


fun createSqlClient(config: JsonObject) : SqlClient =
    PgPool.client(getPgConnectOptions(config), getPoolOptions(config))

fun getPgConnectOptions(config: JsonObject) : PgConnectOptions {
    val pgConfig = config.getJsonObject("postgres") ?: throw Exception("'postgres' config block should be provided")
    return pgConnectOptionsOf(
        host = pgConfig.getString("host") ?: "localhost",
        port = pgConfig.getInteger("port") ?: 5432,
        database = pgConfig.getString("db") ?: "postgres",
        user =
        config.getString("PG_USERNAME") ?:
        pgConfig.getString("username") ?:
        throw Exception("Either PG_USERNAME or postgres.username should be provided"),
        password =
        config.getString("PG_PASSWORD") ?:
        pgConfig.getString("password") ?:
        throw Exception("Either PG_PASSWORD or postgres.password should be defined"),
        reconnectAttempts = 3,
        reconnectInterval = 1000
    )
}

private fun getPoolOptions(config: JsonObject) = poolOptionsOf(
    maxSize = config.getJsonObject("postgres")?.getInteger("max-pool-size") ?: 5
)
