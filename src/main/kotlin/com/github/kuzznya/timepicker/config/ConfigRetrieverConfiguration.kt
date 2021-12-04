package com.github.kuzznya.timepicker.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf

fun createConfigRetriever(vertx: Vertx) : ConfigRetriever =
    ConfigRetriever.create(vertx, retrieverOptions())

private fun retrieverOptions() : ConfigRetrieverOptions = configRetrieverOptionsOf(includeDefaultStores = true)
    .addStore(yamlStoreOptions())
    .addStore(envStoreOptions())

private fun yamlStoreOptions() : ConfigStoreOptions = configStoreOptionsOf(
    type = "file",
    format = "yaml",
    config = JsonObject().put("path", "config.yml")
)

private fun envStoreOptions() : ConfigStoreOptions = configStoreOptionsOf(type = "env")
