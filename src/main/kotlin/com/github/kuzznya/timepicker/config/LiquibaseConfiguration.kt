package com.github.kuzznya.timepicker.config

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.resource.FileSystemResourceAccessor
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private val log = LoggerFactory.getLogger("com.github.kuzznya.timepicker.config.LiquibaseConfiguration")
private val migrationsRan = AtomicBoolean(false)

fun runMigrations(vertx: Vertx, config: JsonObject): Future<Unit> {
    return if (migrationsRan.compareAndSet(false, true)) {
        vertx.executeBlocking { promise ->
            runMigrationsBlocking(config)
            promise.complete()
        }
    } else {
        log.warn("Liquibase migration already started or ran, skipping execution")
        Future.succeededFuture()
    }
}

private fun runMigrationsBlocking(config: JsonObject) {
    val options = getPgConnectOptions(config)
    val changeLogFile = config.getJsonObject("postgres")?.getString("changelog") ?: "classpath:db.changelog-master.xml"
    var connection: Connection? = null
    try {
        val url = "jdbc:postgresql://${options.host}:${options.port}/${options.database}"
        val props = Properties()
        props.setProperty("user", options.user)
        props.setProperty("password", options.password)
        connection = DriverManager.getConnection(url, props)
        val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
        val liquibase = Liquibase(
            changeLogFile,
            CompositeResourceAccessor(ClassLoaderResourceAccessor(), FileSystemResourceAccessor()),
            database)
        liquibase.update(Contexts(), LabelExpression())
        log.info("Liquibase ran successfully")
    } catch (e: Exception) {
        log.error("Liquibase migration error", e)
        throw e
    } finally {
        checkConnection(connection)
    }
}

private fun checkConnection(connection: Connection?) {
    if (connection == null) return
    log.info("Connection is not null, starting rollback & close")
    try {
        connection.rollback()
        log.info("Rollback finished")
        connection.close()
        log.info("Connection closed")
    } catch (e: Exception) {
        log.error("Rollback error", e)
    }
}
