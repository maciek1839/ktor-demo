package com.showmeyourcode.ktor.demo.response

import com.showmeyourcode.ktor.demo.common.DATE_FORMAT
import com.showmeyourcode.ktor.demo.user.UserCount
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.joda.time.DateTime

@Serializable
data class ErrorMessage(
    val status: Int,
    val message: String,
    val timestamp: String = DateTime.now().toString(DATE_FORMAT)
)

@Serializable
data class ServerResponse(
    val data: UserCount,
    val requestedBy: String,
    val timestamp: String = DateTime.now().toString(DATE_FORMAT)
)

fun ServerResponse.toJson() = DefaultJson.encodeToString(this)

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

suspend fun writeRequestError(call: ApplicationCall, msg: String, status: HttpStatusCode) {
    call.respond(status, ErrorMessage(status = status.value, message = msg))
}
