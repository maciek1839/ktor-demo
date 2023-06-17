package com.showmeyourcode.ktor.demo.response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
data class ErrorMessage(
    val timestamp: String = DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss.SSZZ"),
    val status: Int,
    val message: String
)

fun getDefaultNotFoundResponse(msg: String): ErrorMessage {
    return ErrorMessage(status = HttpStatusCode.NotFound.value, message = msg)
}

fun getDefaultBadRequest(msg: String): ErrorMessage {
    return ErrorMessage(status = HttpStatusCode.BadRequest.value, message = msg)
}

fun getDefaultInternalServerError(msg: String): ErrorMessage {
    return ErrorMessage(status = HttpStatusCode.InternalServerError.value, message = msg)
}

suspend fun writeDefaultInternalServerError(call: ApplicationCall, msg: String) {
    call.respond(HttpStatusCode.InternalServerError, getDefaultInternalServerError(msg))
}

suspend fun writeDefaultBadRequestError(call: ApplicationCall, msg: String) {
    call.respond(HttpStatusCode.BadRequest, getDefaultBadRequest(msg))
}

suspend fun writeDefaultNotFoundRequestError(call: ApplicationCall, msg: String) {
    call.respond(HttpStatusCode.NotFound, getDefaultNotFoundResponse(msg))
}
