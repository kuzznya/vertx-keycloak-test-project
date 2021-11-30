package com.github.kuzznya.timepicker.exception

import io.netty.handler.codec.http.HttpResponseStatus

open class ResponseStatusException: Exception {

    val status: HttpResponseStatus

    constructor(status: HttpResponseStatus) : super() {
        this.status = status
    }

    constructor(status: HttpResponseStatus, message: String) : super(message) {
        this.status = status
    }

    constructor(status: HttpResponseStatus, cause: Throwable) : super(cause) {
        this.status = status
    }

    constructor(status: HttpResponseStatus, message: String, cause: Throwable) : super(message, cause) {
        this.status = status
    }
}
