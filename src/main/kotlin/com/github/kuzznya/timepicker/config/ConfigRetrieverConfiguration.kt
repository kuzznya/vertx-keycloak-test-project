package com.github.kuzznya.timepicker.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

fun createConfigRetriever(vertx: Vertx) : ConfigRetriever =
    ConfigRetriever.create(vertx, retrieverOptions())

private fun retrieverOptions() : ConfigRetrieverOptions = ConfigRetrieverOptions()
    .setIncludeDefaultStores(true)
    .addStore(yamlStoreOptions())
    .addStore(envStoreOptions())

private fun yamlStoreOptions() : ConfigStoreOptions = ConfigStoreOptions()
    .setType("file")
    .setFormat("yaml")
    .setConfig(JsonObject().put("path", "config.yml"))

private fun envStoreOptions() : ConfigStoreOptions = ConfigStoreOptions().setType("env")
