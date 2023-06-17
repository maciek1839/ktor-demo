package com.showmeyourcode.ktor.demo.user

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

data class User(
    val id: Int,
    val email: String,
    val nick: String,
    val active: Boolean,
    val password: String,
    val createAt: String
)

@Serializable
data class RegisterUser(
    val email: String?,
    val password: String?,
    val nick: String?
)

val mapper = DefaultJson

fun RegisterUser.toJson(): String {
    return mapper.encodeToString(this)
}

@Serializable
data class LoginUser(
    val email: String,
    val password: String
)

fun LoginUser.toJson(): String {
    return mapper.encodeToString(this)
}

fun isEmailValid(email: String): Boolean {
    return "^[A-Za-z](.*)([@])(.+)(\\.)(.+)".toRegex().matches(email)
}
